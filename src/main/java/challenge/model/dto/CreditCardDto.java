package challenge.model.dto;

import challenge.model.enums.Brand;
import challenge.model.enums.Status;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

public record CreditCardDto(
        String id,

        @NotBlank(message = "customer is mandatory")
        String customer,

        @NotNull(message = "number is mandatory")
        @Min(message = "number is lower than minimum", value = 1000000000000000L)
        @Max(message = "number is higher than maximum", value = 9999999999999999L)
        Long number,

        @NotNull(message = "brand is mandatory")
        Brand brand,

        Status status,

        LocalDateTime createdDate
) {
}
