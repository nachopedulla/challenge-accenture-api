package challenge.model.dto.criteria;

import challenge.model.enums.Brand;
import challenge.model.enums.Status;

public record CreditCardCriteriaDto(
        String customer,
        Status status
) {
}
