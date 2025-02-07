package challenge.unitary;

import challenge.exception.CardAlreadyCloseException;
import challenge.exception.CreditCardAlreadyExistsException;
import challenge.exception.CreditCardNotFoundException;
import challenge.model.dto.CreditCardDto;
import challenge.model.entity.CreditCard;
import challenge.model.enums.Brand;
import challenge.model.enums.Status;
import challenge.repository.CreditCardRepository;
import challenge.service.CreditCardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

class CreditCardServiceTest {

    private static final String CUSTOMER_ID = "customer_id";
    private CreditCardRepository creditCardRepository;
    private CreditCardService creditCardService;

    private static final String CARD_ID = "60f2adb9-865a-4b8a-8a83eb";
    private static final Long CARD_NUMBER = 4578122134435665L;

    @BeforeEach
    void setUp() {
        creditCardRepository = Mockito.mock(CreditCardRepository.class);
        creditCardService = new CreditCardService(creditCardRepository);
    }

    @Test
    void testCardNotFoundByIdThrowsException() {
        Mockito.when(creditCardRepository.findById(CARD_ID)).thenReturn(Optional.empty());
        Assertions.assertThrows(CreditCardNotFoundException.class, () -> creditCardService.getById(CARD_ID));
    }

    @Test
    void testCardIsFoundById() {
        var card = card(Status.ACTIVE);
        Mockito.when(creditCardRepository.findById(CARD_ID)).thenReturn(Optional.of(card));
        Assertions.assertEquals(card, creditCardService.getById(CARD_ID));
    }

    @Test
    void testExistingCardOnCreationThrowsException() {
        var request = cardDto();
        Mockito.when(creditCardRepository.findByNumber(CARD_NUMBER)).thenReturn(Optional.of(card(Status.ACTIVE)));
        Assertions.assertThrows(CreditCardAlreadyExistsException.class, () -> creditCardService.create(request));
    }

    @Test
    void testCardAlreadyInactiveThrowsException() {
        var card = card(Status.INACTIVE);
        Mockito.when(creditCardRepository.findById(CARD_ID)).thenReturn(Optional.of(card));
        Assertions.assertThrows(CardAlreadyCloseException.class, () -> creditCardService.remove(CARD_ID));
    }

    @Test
    void testCardCreation() {
        Mockito.when(creditCardRepository.findByNumber(CARD_NUMBER)).thenReturn(Optional.empty());
        mockRepositorySave();

        var card = creditCardService.create(cardDto());

        Mockito.verify(creditCardRepository, Mockito.times(1)).save(Mockito.any());
        Assertions.assertNotNull(card);
        Assertions.assertNotNull(card.getId());
    }

    @Test
    void testCardRemoval() {
        Mockito.when(creditCardRepository.findById(CARD_ID)).thenReturn(Optional.of(card(Status.ACTIVE)));
        mockRepositorySave();

        var card = creditCardService.remove(CARD_ID);
        Assertions.assertEquals(Status.INACTIVE, card.getStatus());
        Assertions.assertEquals(LocalDate.now(), card.getLastModifiedDate().toLocalDate());
    }

    @Test
    void testCardUpdateWithAlreadyUsedNumber() {
        var card = card(Status.ACTIVE);
        Mockito.when(creditCardRepository.findByNumber(CARD_NUMBER)).thenReturn(Optional.of(card));
        Mockito.when(creditCardRepository.findById(CARD_ID)).thenReturn(Optional.of(new CreditCard()));

        var request = cardDto();
        Assertions.assertThrows(CreditCardAlreadyExistsException.class, () -> creditCardService.update(CARD_ID, request));
    }

    @Test
    void testCardIsUpdate() {
        var card = card(Status.ACTIVE);
        Mockito.when(creditCardRepository.findById(CARD_ID)).thenReturn(Optional.of(card));
        mockRepositorySave();

        var request = cardDto();
        Assertions.assertNotNull(creditCardService.update(CARD_ID, request));
    }

    private CreditCard card(Status status) {
        var card = new CreditCard();
        card.setId(CARD_ID);
        card.setCustomer(CUSTOMER_ID);
        card.setNumber(CARD_NUMBER);
        card.setBrand(Brand.VISA);
        card.setStatus(status);
        return card;
    }

    private CreditCardDto cardDto() {
        return new CreditCardDto(
                null,
                CUSTOMER_ID,
                CARD_NUMBER,
                Brand.VISA,
                Status.ACTIVE,
                LocalDateTime.now()
        );
    }

    private void mockRepositorySave() {
        Mockito.when(creditCardRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);
    }
}