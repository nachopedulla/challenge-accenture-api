package challenge.controller.error;

public record ErrorApiDto (
        Integer code,
        String type,
        String message
) {
}
