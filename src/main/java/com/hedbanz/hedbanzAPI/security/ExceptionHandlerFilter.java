package com.hedbanz.hedbanzAPI.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedbanz.hedbanzAPI.constant.ResultStatus;
import com.hedbanz.hedbanzAPI.error.CustomError;
import com.hedbanz.hedbanzAPI.exception.AuthenticationException;
import com.hedbanz.hedbanzAPI.exception.NotFoundException;
import com.hedbanz.hedbanzAPI.model.ResponseBody;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class ExceptionHandlerFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException | NotFoundException e) {
            ResponseBody<CustomError> responseBody = new ResponseBody<>(
                    ResultStatus.ERROR_STATUS,
                    new CustomError(e.getCode(), e.getMessage()), null
            );
            response.getWriter().write(convertObjectToJson(responseBody));
            response.setContentType("application/json");
            response.setStatus(HttpStatus.OK.value());
            logger.error(e.getCode() + " : " + e.getMessage());
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
