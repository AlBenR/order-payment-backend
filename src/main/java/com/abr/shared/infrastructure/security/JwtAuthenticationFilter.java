package com.abr.shared.infrastructure.security;

import com.abr.shared.application.security.AuthenticatedUser;
import com.abr.auth.infrastructure.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!tokenProvider.isValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = tokenProvider.parseClaims(token);

        UUID userId = UUID.fromString(claims.getSubject());
        String username = claims.get("username", String.class);

        List<String> roles = claims.get("roles", List.class);

        Set<String> roleSet = roles.stream().collect(Collectors.toSet());

        AuthenticatedUser authenticatedUser =
                new AuthenticatedUser(userId, username, roleSet);

        var authorities = roleSet.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        var authentication = new UsernamePasswordAuthenticationToken(
                authenticatedUser,
                null,
                authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}