package com.erp.gateway.config;


import com.erp.shared_data.exception.BadRequestException;
import com.erp.shared_data.exception.ErrorResponse;
import com.erp.shared_data.exception.ExceptionMessageCode;
import com.erp.shared_data.exception.FeignClientException;
import com.erp.shared_data.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionsHandler {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ErrorResponse handle(BadRequestException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                ExceptionMessageCode.BAD_REQUEST_EXCEPTION_MESSAGE.getMessage(),
                request.getRequestURI()
        );
        log.error("======= [handle][BadRequestException] errorResponse = {}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ErrorResponse handle(AccessDeniedException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                exception.getMessage(),
                ExceptionMessageCode.FORBIDDEN_EXCEPTION_MESSAGE.getMessage(),
                request.getRequestURI()
        );
        log.error("======= [handle][AccessDeniedException] errorResponse = {}", errorResponse);
        return errorResponse;
    }

    @ExceptionHandler(FeignClientException.class)
    public ResponseEntity<ErrorResponse> handle(FeignClientException exception) {
        ErrorResponse errorResponse = exception.getErrorResponse();
        log.error("======= [handle][FeignClientException] errorResponse = {}", errorResponse);
        return ResponseEntity
                .status(Objects.requireNonNull(HttpStatus.resolve(errorResponse.getStatus())))
                .body(errorResponse);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ErrorResponse handle(ConstraintViolationException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                ExceptionMessageCode.BAD_REQUEST_EXCEPTION_MESSAGE.getMessage(),
                request.getRequestURI()
        );
        log.error("======= [handle][BadRequestException] errorResponse = {}", errorResponse);
        return errorResponse;
    }


    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ErrorResponse handle(HttpMessageNotReadableException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                ExceptionMessageCode.BAD_REQUEST_EXCEPTION_MESSAGE.getMessage(),
                request.getRequestURI()
        );
        log.error("======= [handle][BadRequestException] errorResponse = {}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ErrorResponse handle(MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                ExceptionMessageCode.BAD_REQUEST_EXCEPTION_MESSAGE.getMessage(),
                request.getRequestURI()
        );
        log.error("======= [handle][BadRequestException] errorResponse = {}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ErrorResponse handle(MissingServletRequestParameterException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                ExceptionMessageCode.BAD_REQUEST_EXCEPTION_MESSAGE.getMessage(),
                request.getRequestURI()
        );
        log.error("======= [handle][BadRequestException] errorResponse = {}", errorResponse);
        return errorResponse;
    }


    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ErrorResponse handle(NotFoundException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                ExceptionMessageCode.NOT_FOUND_EXCEPTION_MESSAGE.getMessage(),
                request.getRequestURI()
        );
        log.error("======= [handle][NotFoundException] errorResponse = {}", errorResponse);
        return errorResponse;
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorResponse handle(Exception exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getMessage(),
                ExceptionMessageCode.INTERNAL_SERVER_ERROR_MESSAGE.getMessage(),
                request.getRequestURI()
        );
        log.error("======= [handle][InternalServerError] errorResponse = {}", errorResponse);
        log.error(exception.toString(), exception);
        return errorResponse;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorResponse handle(MethodArgumentNotValidException exception, HttpServletRequest request) {
        return handle(new BadRequestException(exception.getMessage()), request);
    }
}
