package com.andela.tms.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    private static Map<String, Object> getErrorBody(HttpStatusCode status, List<Map<String, String>> errors){
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("status", status.value());
        body.put("error", "Bad Request");
        body.put("message", "Validation failed");
        body.put("errors", errors);
        return body;
    }

    public static ResponseEntity<Object> getValidationErrorResponseEntity(HttpStatusCode status,
                                                                          List<Map<String, String>> errors){

        return new ResponseEntity<>(getErrorBody(status,errors), status);
    }

    public static ResponseEntity<Object> getValidationErrorResponseEntity(HttpHeaders headers, HttpStatusCode status,
                                                                List<Map<String, String>> errors){
        return new ResponseEntity<>(getErrorBody(status, errors), headers, status);
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {


        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("field", error.getField());
                    errorMap.put("message", error.getDefaultMessage());
                    return errorMap;
                })
                .toList();
        return getValidationErrorResponseEntity(headers, status, errors);

    }

}

