package com.hss.hss_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/public/**", "/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/api/auth/sync").authenticated()
                        .requestMatchers("/api/clinics/**").hasRole("SUPER_ADMIN") // Secure clinics endpoint
                        // Swagger UI and API docs
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**")
                        .permitAll()
                        // Public endpoints
                        .requestMatchers("/api/species/all").permitAll()
                        .requestMatchers("/api/breeds/species/**").permitAll()
                        .requestMatchers("/api/owners").permitAll()
                        // Protected endpoints
                        .requestMatchers("/api/animals/**").permitAll() // TODO: Review permissions
                        .requestMatchers("/api/appointments/**").permitAll() // TODO: Review permissions
                        .requestMatchers("/api/medical-history/**").hasAnyRole("ADMIN", "VETERINARIAN")
                        .requestMatchers("/api/lab-tests/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF")
                        .requestMatchers("/api/prescriptions/**").hasAnyRole("ADMIN", "VETERINARIAN")
                        .requestMatchers("/api/vaccinations/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF")
                        .requestMatchers("/api/invoices/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/api/inventory/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/stock/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF")
                        .requestMatchers("/api/files/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF")
                        .requestMatchers("/api/dashboard/**")
                        .hasAnyRole("ADMIN", "VETERINARIAN", "STAFF", "RECEPTIONIST")
                        .requestMatchers("/api/species/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF", "RECEPTIONIST")
                        .requestMatchers("/api/owners/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF", "RECEPTIONIST")
                        .requestMatchers("/api/staff/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        // Custom authorities converter to extract roles from claims
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // Extract 'roles' claim (custom claim set via Firebase Admin SDK)
            Object rolesClaim = jwt.getClaim("roles");
            if (rolesClaim instanceof List) {
                List<?> rolesList = (List<?>) rolesClaim;
                for (Object role : rolesList) {
                    if (role instanceof String) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                }
            }

            // Extract 'role' claim (single role scenario)
            Object roleClaim = jwt.getClaim("role");
            if (roleClaim instanceof String) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleClaim));
            }

            return authorities;
        });

        return jwtAuthenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow all origin patterns to support:
        // - Admin portal: admin.hssvet.com (or admin.localhost for local dev)
        // - Customer portal: hssvet.com (or localhost for local dev)
        // - Clinic-specific paths: hssvet.com/klinikadi/*
        // TODO: For production, replace with explicit allowed origins for better
        // security
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder() {
        return token -> {
            java.util.Map<String, Object> claims = new java.util.HashMap<>();
            claims.put("sub", "test-user");
//            claims.put("iss", "https://securetoken.google.com/hss-cloud-473511"); // Optional for now
            // Dev-only dummy roles: allow admin + super-admin screens locally.
            // NOTE: In production, this MUST be removed and replaced with real JWT verification.
            claims.put("roles", java.util.List.of("SUPER_ADMIN", "ADMIN", "VETERINARIAN", "STAFF")); // Grant full access
            
            // Return a dummy validated JWT
            // Note: In production, this MUST be removed
            return new org.springframework.security.oauth2.jwt.Jwt(
                    token, 
                    java.time.Instant.now(), 
                    java.time.Instant.now().plusSeconds(3600), 
                    java.util.Map.of("alg", "none"), 
                    claims
            );
        };
    }
}
