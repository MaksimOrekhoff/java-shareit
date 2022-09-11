package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(UserExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse validationExceptionHandler(final UserExistException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse validationExceptionHandler(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
