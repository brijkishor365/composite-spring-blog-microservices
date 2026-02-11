package com.qburst.microservice.auth.security;

import com.qburst.microservice.auth.exception.auth.JwtAuthenticationException;
import com.qburst.microservice.auth.exception.base.JwtErrorType;
import com.qburst.microservice.auth.repository.BlacklistedTokenRepository;
import com.qburst.microservice.auth.service.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public JwtFilter(JwtService jwtService,
                     UserDetailsService userDetailsService,
                     BlacklistedTokenRepository blacklistedTokenRepository,
                     AuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Optional blacklist check
            if (blacklistedTokenRepository.existsByToken(token)) {
                throw new JwtAuthenticationException(
                        JwtErrorType.BLACKLISTED,
                        "Token is blacklisted",
                        null
                );
            }

            String username = jwtService.extractUsername(token);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username); // TODO:: Hits DB on every request, need to fix

                if (!jwtService.isTokenValid(token, userDetails)) {
                    throw new JwtAuthenticationException(
                            JwtErrorType.SIGNATURE_INVALID,
                            "Invalid JWT token",
                            null
                    );
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Add userID in log once authenticated
                MDC.put("userId", userDetails.getUsername());
            }

            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException ex) {
            SecurityContextHolder.clearContext();
            throw ex;
        } catch (SignatureException ex) {
            SecurityContextHolder.clearContext();
            throw new JwtAuthenticationException(
                    JwtErrorType.SIGNATURE_INVALID,
                    "JWT signature invalid",
                    ex
            );
        } catch (ExpiredJwtException ex) {
            SecurityContextHolder.clearContext();
            throw new JwtAuthenticationException(
                    JwtErrorType.EXPIRED,
                    "JWT expired",
                    ex
            );
        } catch (MalformedJwtException | UnsupportedJwtException ex) {
            SecurityContextHolder.clearContext();
            throw new JwtAuthenticationException(
                    JwtErrorType.MALFORMED,
                    "Malformed JWT",
                    ex
            );
        } finally {
            MDC.clear(); // VERY important
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        // Exclude actuator paths from JWT validation logic
        return path.startsWith("/actuator")
                || path.startsWith("/manage")
                || path.startsWith("/swagger-ui/index.html")
                || path.startsWith("/v3/api-docs");
    }
}
