package challenge.specification;

import challenge.model.dto.criteria.CreditCardCriteriaDto;
import challenge.model.entity.CreditCard;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreditCardSpecification {

    private static final String CUSTOMER = "customer";
    private static final String STATUS = "status";

    public static Specification<CreditCard> get(CreditCardCriteriaDto criteria) {
        Specification<CreditCard> spec = Specification.where((root, query, builder) ->
                builder.equal(root.get(CUSTOMER), criteria.customer())
        );

        if (criteria.status() != null) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get(STATUS), criteria.status()));
        }

        return spec;
    }
}