package com.swd392.mentorbooking.jwt;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JWTService jwtService;
    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token= null;
        String userName = null;
        String uri = request.getRequestURI();
        try {
            if (uri.contains("/auth/login") || uri.contains("/auth/register") || uri.contains("/auth/forgot-password")) {
                filterChain.doFilter(request, response);
                return;
            }
            if (authHeader != null && authHeader.startsWith("Bearer")) {
                token = authHeader.substring(7);
                userName = jwtService.extractEmail(token);
            }
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Account accountEntity = authService.findAccountByEmail(userName);
                if (jwtService.validateToken(token, accountEntity)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(accountEntity, null, accountEntity.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            // Token hết hạn
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token has expired!");
        } catch (JwtException | IllegalArgumentException ex) {
            // Token không hợp lệ
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid token!");
        }
    }
}
