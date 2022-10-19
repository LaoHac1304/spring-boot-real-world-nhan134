package com.codevui.realworldapp.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.codevui.realworldapp.entity.User;
import com.codevui.realworldapp.model.user.TokenPayload;
import com.codevui.realworldapp.repository.UserRepository;
import com.codevui.realworldapp.util.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String token = null;
        TokenPayload tokenPayload = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Token ")) {
            token = requestTokenHeader.substring(6).trim();

            try {
                tokenPayload = jwtTokenUtil.getTokenPayLoad(token);
            } catch (SignatureException e) {
                System.out.println("invalid JWT signature");
            } catch (IllegalArgumentException ex) {
                System.out.println("Unable to get JWT");
            } catch (ExpiredJwtException ex) {
                System.out.println("Token had expired");
            }

        } else {
            System.out.println("JWT Token does not start with 'Token '");
        }
        // luu thong tin nguoi dung vao ContextHolder(Session)
        if (tokenPayload != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<User> userOptional = userRepository.findById(tokenPayload.getUserID());

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // check token co hop le hay khong
                if (jwtTokenUtil.vailidate(token, user)) {

                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(),
                            user.getPassword(), authorities);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);

    }

}
