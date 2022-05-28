package com.stsl.authservice.custom;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.stsl.authservice.response.BaseResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException, ServletException {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setErrors(ex.getMessage());
        baseResponse.setDescription(ex.getLocalizedMessage());
        baseResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.println(new ObjectMapper().writeValueAsString(baseResponse));
        out.flush();
    }
}
