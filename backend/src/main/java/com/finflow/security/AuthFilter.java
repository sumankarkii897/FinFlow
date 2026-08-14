package com.finflow.security;

import com.finflow.exceptions.CustomAuthenticationEntryPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Incoming request : {}", request.getRequestURL());
        String token = getTokenFromRequest(request);
        if (token != null) {
            String email;
            try{
                email = jwtUtils.getUsernameFromToken(token);
            }
            catch (Exception e){
                log.error("Error while getting username from token : {}",e.getMessage());
                AuthenticationException authenticationException =
                        new BadCredentialsException(e.getMessage());
                customAuthenticationEntryPoint.commence(request,
                        response,authenticationException);
                return;
            }
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            if(StringUtils.hasText(email) &&
                    jwtUtils.isTokenValid(token,userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken
                        (userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        try{
            filterChain.doFilter(request,response);
        }
        catch (Exception e){
            log.error("Error while processing request : {}",e.getMessage());
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authenticationHeader = request.getHeader("Authorization");
        if (authenticationHeader != null && authenticationHeader.startsWith("Bearer ")) {
            return authenticationHeader.substring(7);
        }
        return null;
    }
}
