# Multi-stage build for HSS Backend
FROM eclipse-temurin:21-jdk AS build

# Set working directory
WORKDIR /workspace/app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests -T 1C

# Runtime stage
FROM eclipse-temurin:21-jre

# Install necessary packages (curl for healthcheck)
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Create app user
RUN groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -M -s /usr/sbin/nologin appuser

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /workspace/app/target/*.jar app.jar

# Change ownership to app user
RUN chown -R appuser:appgroup /app

# Switch to app user
USER appuser

# Expose port (Cloud Run will set PORT environment variable)
EXPOSE 8080

# Health check (use PORT environment variable) - Cloud Run için optimize edildi
HEALTHCHECK --interval=30s --timeout=10s --start-period=180s --retries=3 \
    CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application with proper JVM settings for Cloud Run
# NOTE: Cloud Run env var precedence / revision config may still result in a "prod"
# profile being activated. For now we hard-pin the active profile to "dev" to
# ensure startup succeeds while we align prod DB & Flyway state.
ENTRYPOINT ["sh", "-c", "exec java -Dserver.port=${PORT:-8080} -Dserver.address=0.0.0.0 -Dio.netty.handler.ssl.noOpenSsl=true -Xmx1536m -XX:+UseG1GC -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=dev -jar app.jar"]
