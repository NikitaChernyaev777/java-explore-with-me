package ru.practicum.ewm.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static ru.practicum.ewm.constant.DateTimeFormatters.FORMATTER;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCustomValidationException(final ValidationException exception) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase(),
                "Ошибка бизнес-валидации",
                exception.getMessage(),
                LocalDateTime.now().format(FORMATTER)
        );
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleServletRequestParameterException(
            final MissingServletRequestParameterException exception) {

        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase(),
                "Неправильно сделанный запрос",
                exception.getMessage(),
                LocalDateTime.now().format(FORMATTER)
        );
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable exception) {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase().toUpperCase(),
                "Непредвиденная ошибка",
                exception.getMessage(),
                LocalDateTime.now().format(FORMATTER)
        );
    }
}