package lenicorp.admin.security.config;

import lenicorp.admin.security.audit.AuditContext;
import lenicorp.admin.security.audit.AuditContextHolder;
import lenicorp.admin.security.controller.services.impl.SpringJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final SpringJwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Nettoyage préventif du contexte d'audit en début de traitement
        AuditContextHolder.clear();

        // Skip if no Authorization header or not a Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            try {
                filterChain.doFilter(request, response);
            } finally {
                AuditContextHolder.clear();
            }
            return;
        }

        // Extract token from header
        jwt = authHeader.substring(7);
        
        try {
            // Extract username from token
            userEmail = jwtService.extractUsername(jwt);
            
            // If we have a username and no authentication is set yet
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                // Validate token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // Set details and store the token in the security context
                    authToken.setDetails(jwt); // Store the raw token for later retrieval
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Peupler le contexte d'audit
                    String connexionId = null;
                    try {
                        connexionId = jwtService.extractClaim(jwt, claims -> claims.get("connexionId", String.class));
                    } catch (Exception ignored) { }

                    String ip = extractClientIp(request);
                    String mac = request.getHeader("X-MAC-Address"); // meilleure effort via en-tête personnalisé

                    AuditContextHolder.set(AuditContext.builder()
                            .connexionId(connexionId)
                            .ipAddress(ip)
                            .macAddress(mac)
                            .build());
                }
            }
        } catch (Exception e) {
            // Log exception but continue filter chain
            logger.error("JWT Authentication failed: " + e.getMessage());
        }
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Toujours nettoyer pour éviter les fuites entre requêtes/threads
            AuditContextHolder.clear();
        }
    }

    private String extractClientIp(HttpServletRequest request)
    {
        // Priorité aux en-têtes de proxy/CDN si présents
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // XFF peut contenir plusieurs IP séparées par virgule; on prend la première
            String first = xff.split(",")[0].trim();
            if (!first.isBlank()) return first;
        }
        String xri = request.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) return xri.trim();
        return request.getRemoteAddr();
    }
}