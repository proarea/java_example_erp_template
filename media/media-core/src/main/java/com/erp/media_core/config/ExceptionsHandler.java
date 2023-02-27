package com.erp.media_core.config;

import com.erp.shared_data.exception.BadRequestException;
import com.erp.shared_data.exception.ErrorResponse;
import com.erp.shared_data.exception.ExceptionMessageCode;
import com.erp.shared_data.exception.FileException;
import com.erp.shared_data.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ErrorResponse> handle(FileException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getHttpStatus().value(),
                exception.getMessage(),
                ExceptionMessageCode.FILE_EXCEPTION_MESSAGE.getMessage(),
                request.getRequestURI()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        log.error("======= [handle][FileException] errorResponse = {}", errorResponse);

        return new ResponseEntity<>(errorResponse, headers, exception.getHttpStatus());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorResponse handle(MethodArgumentNotValidException exception, HttpServletRequest request) {
        return handle(new BadRequestException(exception.getMessage()), request);
    }
}
