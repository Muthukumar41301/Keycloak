package com.spring.keycloak.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CustomFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        filterChain.doFilter(servletRequest, servletResponse);

        if(httpServletResponse.getStatus() == HttpStatus.SC_FORBIDDEN) {
            httpServletResponse.getWriter().write("{\n" +
                    "    \"status\": 503,\n" +
                    "    \"error\": \"This resource is only for premium user. Kindly pay first.\",\n" +
                    "    \"message\": \"Your are not authorized for this end-point\"\n" +
                    "}");
            httpServletResponse.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
            httpServletResponse.setContentType("application/json");
        } else {
            logger.info("Response status: {}", httpServletResponse.getStatus());
        }
    }
}
