package com.stsl.notificationservice.custom;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.stsl.notificationservice.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setErrors(authException.getMessage());
        baseResponse.setDescription(authException.getLocalizedMessage());
        baseResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), baseResponse);
        } catch (Exception e) {
            throw new ServletException();
        }
    }
}
