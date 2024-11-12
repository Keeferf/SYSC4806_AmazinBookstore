package com.bookstore.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.ArrayList;
import java.util.Optional;

/**
 * JWTRequestFilter is a filter that intercepts incoming HTTP requests to validate JWT tokens.
 * It extracts the JWT from the Authorization header, validates it using JWTService,
 * and sets the user details in the security context for further authorization checks.
 * This filter ensures that only authenticated requests can access protected resources.
 */
@Component
public class JWTRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JWTService jwtService;
    @Autowired
    private UserRepository userRepository;

    public JWTRequestFilter(JWTService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws jakarta.servlet.ServletException, java.io.IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String tokenWithoutBearer = token.substring(7);
            try {
                String username = jwtService.getUsername(tokenWithoutBearer);
                Optional<User> opUser = userRepository.findByUsernameIgnoreCase(username);
                if(opUser.isPresent()) {
                    User user = opUser.get();
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (JWTDecodeException e) {}

        }
        filterChain.doFilter(request, response);
    }
}
