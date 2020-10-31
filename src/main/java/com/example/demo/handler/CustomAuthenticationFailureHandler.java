package com.example.demo.handler;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception.getClass().isAssignableFrom(DisabledException.class)) {
            response.sendRedirect("/verifyError");
        }
        if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
            response.sendRedirect("/signIn?error");
        }
        if (exception.getClass().isAssignableFrom(LockedException.class)) {
            response.sendRedirect("/lock?error");
        }
    }
}