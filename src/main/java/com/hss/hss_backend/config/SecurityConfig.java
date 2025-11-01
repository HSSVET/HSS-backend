package com.hss.hss_backend.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Test için sabit secret key - her restart'ta aynı key kullanılsın
    private static final SecretKey SECRET_KEY = Keys
            .hmacShaKeyFor("mySecretKeyForTestingPurposesOnly123456789012345678901234567890".getBytes());

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/public/**", "/actuator/health", "/actuator/info").permitAll()
                        // Swagger UI ve API dokümantasyonu için izin ver
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**")
                        .permitAll()
                        .requestMatchers("/api/animals/**").permitAll()
                        .requestMatchers("/api/appointments/**").permitAll()
                        .requestMatchers("/api/medical-history/**").hasAnyRole("ADMIN", "VETERINARIAN")
                        .requestMatchers("/api/lab-tests/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF")
                        .requestMatchers("/api/prescriptions/**").hasAnyRole("ADMIN", "VETERINARIAN")
                        .requestMatchers("/api/vaccinations/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF")
                        .requestMatchers("/api/invoices/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/api/inventory/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/files/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF")
                        .requestMatchers("/api/dashboard/**")
                        .hasAnyRole("ADMIN", "VETERINARIAN", "STAFF", "RECEPTIONIST")
                        // Species ve Owners listesi için GET isteklerine izin ver (hayvan ekleme formu için gerekli)
                        .requestMatchers("/api/species/all").permitAll()
                        .requestMatchers("/api/breeds/species/**").permitAll()
                        .requestMatchers("/api/owners").permitAll() // GET /api/owners
                        .requestMatchers("/api/species/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF", "RECEPTIONIST")
                        .requestMatchers("/api/owners/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF", "RECEPTIONIST")
                        .requestMatchers("/api/staff/**").hasAnyRole("ADMIN", "VETERINARIAN", "STAFF")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Test için basit JWT decoder - AuthController ile aynı secret key kullanıyor
        return NimbusJwtDecoder.withSecretKey(SECRET_KEY).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        // Custom authorities converter kullanarak roles claim'ini işle
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // roles claim'ini al
            Object rolesClaim = jwt.getClaim("roles");
            if (rolesClaim instanceof List) {
                List<?> rolesList = (List<?>) rolesClaim;
                for (Object role : rolesList) {
                    if (role instanceof String) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                }
            }

            return authorities;
        });

        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
