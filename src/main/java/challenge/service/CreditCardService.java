package challenge.service;

import challenge.exception.CardAlreadyCloseException;
import challenge.exception.CreditCardAlreadyExistsException;
import challenge.exception.CreditCardNotFoundException;
import challenge.mapper.CreditCardMapper;
import challenge.model.dto.CreditCardDto;
import challenge.model.dto.criteria.CreditCardCriteriaDto;
import challenge.model.entity.CreditCard;
import challenge.model.enums.Status;
import challenge.repository.CreditCardRepository;
import challenge.specification.CreditCardSpecification;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class CreditCardService {

    private static final String NOT_FOUND_ERROR = "Card with id %s was not found";
    private static final String CARD_EXIST_ERROR = "There is already a registered card with the given number";
    private static final String CARD_ALREADY_INACTIVE_ERROR = "Card with id %s is already inactive";

    private final CreditCardRepository creditCardRepository;

    public Page<CreditCard> getCardsByCriteria(CreditCardCriteriaDto criteria, Pageable pageable) {
        var specification = CreditCardSpecification.get(criteria);
        return creditCardRepository.findAll(specification, pageable);
    }

    public CreditCard getById(String id) {
        return creditCardRepository.findById(id)
                .orElseThrow(() -> new CreditCardNotFoundException(NOT_FOUND_ERROR.formatted(id)));
    }

    public CreditCard create(CreditCardDto request) {
        validateExistingCard(request.number());

        var card = CreditCardMapper.map(request);
        card.setId(UUID.randomUUID().toString());
        card.setCreatedDate(LocalDateTime.now());

        return creditCardRepository.save(card);
    }

    private void validateExistingCard(Long number) {
        /* no chequea estado porque una vez cerrada no se puede volver a crear */
        if (creditCardRepository.findByNumber(number).isPresent()) {
            throw new CreditCardAlreadyExistsException(CARD_EXIST_ERROR);
        }
    }

    public CreditCard remove(String id) {
        var card = getById(id);

        validateClosedCard(card);

        card.setStatus(Status.INACTIVE);
        card.setLastModifiedDate(LocalDateTime.now());
        return creditCardRepository.save(card);
    }

    public CreditCard update(String id, @Valid CreditCardDto request) {
        var card = getById(id);

        /* It could be a list of rules to apply and verify */

        validateClosedCard(card);

        /* If number changes, validates it's not in use */
        if (!Objects.equals(card.getNumber(), request.number())) {
            validateExistingCard(request.number());
        }

        /* status is not updated, it should be done by delete */
        card.setNumber(request.number());
        card.setBrand(request.brand());
        card.setCustomer(request.customer());
        card.setLastModifiedDate(LocalDateTime.now());

        return creditCardRepository.save(card);
    }

    private static void validateClosedCard(CreditCard card) {
        if (Status.INACTIVE == card.getStatus()) {
            throw new CardAlreadyCloseException(CARD_ALREADY_INACTIVE_ERROR.formatted(card.getId()));
        }
    }

}
