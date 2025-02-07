package challenge.mapper;

import challenge.model.dto.CreditCardDto;
import challenge.model.entity.CreditCard;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreditCardMapper {

    public static CreditCard map(CreditCardDto cardDto) {
        var card = new CreditCard();
        card.setBrand(cardDto.brand());
        card.setNumber(cardDto.number());
        card.setCustomer(cardDto.customer());
        return card;
    }
}
