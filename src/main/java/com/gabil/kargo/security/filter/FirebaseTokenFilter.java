package com.gabil.kargo.security.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gabil.kargo.user.User;
import com.gabil.kargo.user.UserService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private final UserService userService; 

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            if (FirebaseApp.getApps().isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);

            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();

            User user = userService.loadOrCreateUser(uid, email,name);

            List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority(user.getRole().getRoleName()));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, authorities);

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (FirebaseAuthException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired Firebase ID token");
            return;

        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Authentication error: " + ex.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
