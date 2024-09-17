package com.spring.keycloak.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        response.getWriter().write("{\n" +
                "    \"status\": " + HttpStatus.SC_UNAUTHORIZED + ",\n" +
                "    \"error\": \"" + authException.getMessage() + "\",\n" +
                "    \"message\": \"" + "Your token is expired, Please generate a new one." + "\",\n" +
                "}");
        response.setHeader(HTTP.CONTENT_TYPE, "application/json");
    }
}
