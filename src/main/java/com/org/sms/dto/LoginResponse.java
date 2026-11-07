package com.org.sms.dto;

public record LoginResponse(String token, String tokenType, String email, String role) {
}
