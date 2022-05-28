package com.stsl.authservice.custom;

import com.stsl.authservice.response.BaseResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        BaseResponse response = new BaseResponse();
        String operation = ((ServletWebRequest) request).getRequest().getRequestURI();
        response.setDescription("error occurred while processing " + operation);
        response.setErrors(ex.getMessage());
        response.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
                    + violation.getMessage());
        }

        BaseResponse response = new BaseResponse();
        response.setDescription(ex.getLocalizedMessage());
        response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setErrors(errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        List<String> errors = Arrays.asList(";".split(Objects.requireNonNull(ex.getMessage())));
        String msg = Objects.requireNonNull(ex.getRootCause()).getMessage().split(" for ")[0] + " , please enter new value";

        BaseResponse response = new BaseResponse();
        response.setDescription(msg);
        response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setErrors(errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException ex) {
        String msg = ex.getMessage();

        BaseResponse response = new BaseResponse();
        response.setDescription(msg);
        response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setErrors(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        BaseResponse response = new BaseResponse();
        response.setDescription(ex.getMessage());
        response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

        BaseResponse response = new BaseResponse();
        response.setDescription(ex.getLocalizedMessage());
        response.setStatusCode(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        response.setErrors(builder.toString());
        return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BaseResponse response = new BaseResponse();
        response.setDescription("Malformed JSON request");
        response.setErrors(ex.getMessage());
        response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        for (HttpMethod t : Objects.requireNonNull(ex.getSupportedHttpMethods())) {
            builder.append(t).append(" ");
        }
        BaseResponse response = new BaseResponse();
        response.setDescription(ex.getLocalizedMessage());
        response.setStatusCode(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.setErrors(builder.toString());
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        String message = "validation failed for " + ex.getParameter().getParameterName() + " in " + ex.getParameter();
        BaseResponse response = new BaseResponse();
        response.setDescription(message);
        response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setErrors(errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,  WebRequest request) {
        String error = ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName();
        BaseResponse response = new BaseResponse();
        response.setDescription(error);
        response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BaseResponse response = new BaseResponse();
        response.setDescription(ex.getLocalizedMessage());
        response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        BaseResponse response = new BaseResponse();
        response.setDescription(error);
        response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BaseResponse response = new BaseResponse();
        response.setDescription(ex.getLocalizedMessage());
        response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        BaseResponse response = new BaseResponse();
        response.setDescription(error);
        response.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
