package com.hedbanz.hedbanzAPI.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.error.CustomError;
import com.hedbanz.hedbanzAPI.exception.AuthenticationException;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExceptionHandlerFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            ResponseBody<CustomError> responseBody = new ResponseBody<>(
                    ResultStatus.ERROR_STATUS,
                    new CustomError(e.getCode(), e.getMessage()), null
            );
            response.getWriter().write(convertObjectToJson(responseBody));
            response.setContentType("application/json");
            response.setStatus(HttpStatus.OK.value());
        }
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    @Override
    public void destroy() {

    }
}
