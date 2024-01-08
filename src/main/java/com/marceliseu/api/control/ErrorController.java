package com.marceliseu.api.control;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.marceliseu.api.service.ServiceException;

@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    private static final String MESSAGE_TEMPLATE = "Object instance has properties which " +
      "are not allowed by the schema: [\"%s\"]";

    @ExceptionHandler(ServiceException.class)
    public void handle(HttpServletResponse response, ServiceException ex) throws IOException {
        if (ex.getCode() == 0) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        } else {
            response.sendError(ex.getCode(), ex.getMessage());
        }
    }
    
}
