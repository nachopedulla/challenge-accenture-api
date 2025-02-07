package challenge.controller.error;

import challenge.client.exception.InternalCallClientException;
import challenge.exception.BusinessException;
import challenge.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class HttpControllerAdvice {

    private static final String BAD_REQUEST = "bad_request";
    private static final String NOT_FOUND = "not_found";
    private static final String BUSINESS_RULE = "business_rule";
    private static final String UNKNOWN = "unknown";
    private static final String EXTERNAL_CLIENT = "external_client";

    private static final String GENERIC_ERROR_MESSAGE = "An unexpected internal error happened";


    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorApiDto handle(NotFoundException ex) {
        return new ErrorApiDto(
                HttpStatus.NOT_FOUND.value(),
                NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorApiDto handle(BusinessException ex) {
        return new ErrorApiDto(
                HttpStatus.CONFLICT.value(),
                BUSINESS_RULE,
                ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorApiDto handle(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" - "));

        return new ErrorApiDto(
                HttpStatus.BAD_REQUEST.value(),
                BAD_REQUEST,
                errors
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApiDto handle(MissingServletRequestParameterException ex) {
        String errorMessage = "Validation failed: " + ex.getMessage();

        return new ErrorApiDto(
                HttpStatus.BAD_REQUEST.value(),
                BAD_REQUEST,
                errorMessage
        );
    }

    @ExceptionHandler(InternalCallClientException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorApiDto handle(InternalCallClientException ex) {
        return new ErrorApiDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                EXTERNAL_CLIENT,
                ex.getMessage()
        );
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorApiDto handle(Exception ex) {
        log.error("Unknown error: {}", ex.getMessage());
        return new ErrorApiDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                UNKNOWN,
                GENERIC_ERROR_MESSAGE
        );
    }
}
