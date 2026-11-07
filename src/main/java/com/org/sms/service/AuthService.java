package com.org.sms.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.org.sms.dto.LoginRequest;
import com.org.sms.dto.LoginResponse;
import com.org.sms.security.JwtService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String role = user.getAuthorities().iterator().next().getAuthority();
        return new LoginResponse(jwtService.generateToken(user), "Bearer", user.getUsername(), role);
    }
}
