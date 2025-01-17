package com.tourism.tourism_backend.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Log incoming request details
        logger.info("Incoming Request: Method=" + request.getMethod() +
        ", URI=" + request.getRequestURI() +
        ", Query=" + request.getQueryString());

        // Log incoming request details
        System.out.println(GREEN + "Incoming Request: " +
                "Method=" + BLUE + request.getMethod() + RESET +
                ", URI=" + YELLOW + request.getRequestURI() + RESET +
                (request.getQueryString() != null ? ", Query=" + RED + request.getQueryString() + RESET : ""));

        // Proceed with the filter chain
        filterChain.doFilter(request, response);

        // Log outgoing response details
        logger.info("Outgoing Response: Status=" +response.getStatus());

        // Log outgoing response details
        System.out.println(GREEN + "Outgoing Response: " +
                "Status=" + RED + response.getStatus() + RESET);
    }
}
