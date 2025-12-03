package main.exception;

public record ErrorResponse(
        String message,
        String error,
        int status
) {
}
