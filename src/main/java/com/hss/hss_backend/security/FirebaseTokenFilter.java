package com.hss.hss_backend.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    // SecurityConfig ile aynı secret key - her restart'ta aynı key kullanılsın
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("mySecretKeyForTestingPurposesOnly123456789012345678901234567890".getBytes());

    private final FirebaseAuth firebaseAuth;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token == null) {
            log.debug("No Authorization header found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // First, try to verify as Firebase token
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token, true);
            log.debug("Token verified successfully as Firebase token for user: {}", decodedToken.getUid());

            // Extract role from custom claims
            Map<String, Object> claims = decodedToken.getClaims();
            String role = (String) claims.getOrDefault("role", "");
            log.debug("Extracted role from token: {}", role);

            // Create authorities (Spring Security expects ROLE_ prefix for hasRole()
            // checks)
            List<SimpleGrantedAuthority> authorities = Collections.emptyList();
            if (StringUtils.hasText(role)) {
                authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            }

            // Create authentication object
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    decodedToken.getUid(),
                    null,
                    authorities);

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authentication set in security context for user: {} with role: {}",
                    decodedToken.getUid(), role);

        } catch (FirebaseAuthException e) {
            log.debug("Token is not a valid Firebase token, trying JWT token: {}", e.getMessage());
            
            // If Firebase verification fails, try JWT token
            try {
                Claims jwtClaims = Jwts.parser()
                        .verifyWith(SECRET_KEY)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                
                String username = jwtClaims.getSubject();
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) jwtClaims.get("roles");
                
                log.debug("Token verified successfully as JWT token for user: {}", username);
                
                // Create authorities from JWT roles
                List<SimpleGrantedAuthority> authorities = Collections.emptyList();
                if (roles != null && !roles.isEmpty()) {
                    authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .toList();
                }
                
                // Create authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities);
                
                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authentication set in security context for user: {} with roles: {}",
                        username, roles);
                
            } catch (Exception jwtException) {
                log.warn("Token is neither a valid Firebase token nor a valid JWT token: {}", jwtException.getMessage());
                SecurityContextHolder.clearContext();
                // Don't return error here - let the request continue, Spring Security will handle authentication
                // If the endpoint requires authentication, it will return 401
                return;
            }
        } catch (Exception e) {
            log.error("Unexpected error during token verification", e);
            SecurityContextHolder.clearContext();
            // Don't return error here - let the request continue
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}

