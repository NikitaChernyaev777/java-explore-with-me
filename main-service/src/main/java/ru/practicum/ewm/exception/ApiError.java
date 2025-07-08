package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.practicum.ewm.constant.DateTimeFormatters.DATE_TIME_FORMAT;

@Getter
public class ApiError {

    private final List<String> errors;
    private final String message;
    private final String reason;
    private final String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private final LocalDateTime timestamp;

    public ApiError(String message, String reason, String status) {
        this.errors = Collections.emptyList();
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(List<String> errors, String message, String reason, String status) {
        this.errors = errors != null ? errors : Collections.emptyList();
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}