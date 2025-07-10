package ru.practicum.ewm.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;
}