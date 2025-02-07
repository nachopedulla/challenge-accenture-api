package challenge.client.model;

import java.util.List;

/* clase custom porque jackson no deserializa Pageable al usar Page - PageImpl */
public record PageResponseDto<T> (
        List<T> content,
        int totalPages,
        long totalElements,
        int number,
        int size
) {
}
