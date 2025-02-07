package challenge.mapper;

import challenge.model.dto.CreditCardDto;
import challenge.model.entity.CreditCard;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreditCardDtoMapper {

    public static CreditCardDto map(CreditCard creditCard) {
        return new CreditCardDto(
                creditCard.getId(),
                creditCard.getCustomer(),
                creditCard.getNumber(),
                creditCard.getBrand(),
                creditCard.getStatus(),
                creditCard.getCreatedDate()
        );
    }
}
