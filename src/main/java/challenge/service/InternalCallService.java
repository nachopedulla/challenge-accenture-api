package challenge.service;

import challenge.client.InternalCallClient;
import challenge.model.dto.CreditCardDto;
import challenge.model.dto.criteria.CreditCardCriteriaDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InternalCallService {

    private final InternalCallClient client;

    public Page<CreditCardDto> getCards(CreditCardCriteriaDto criteria, Pageable pageable) {
        var cards = client.getCards(criteria, pageable);
        return new PageImpl<>(
                cards.content(),
                pageable,
                cards.totalElements()
        );
    }

}
