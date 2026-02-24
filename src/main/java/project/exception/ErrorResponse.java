package project.exception;

public record ErrorResponse(
        String message,
        String error,
        int status
) {
}
