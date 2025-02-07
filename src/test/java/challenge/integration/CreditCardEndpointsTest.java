package challenge.integration;

import challenge.ChallengeApi;
import challenge.model.dto.CreditCardDto;
import challenge.model.enums.Brand;
import challenge.model.enums.Status;
import challenge.repository.CreditCardRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.function.Consumer;

@SpringBootTest(
    classes = ChallengeApi.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class CreditCardEndpointsTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Test
    void testRequiredAttributesAsNullReturnsBadRequest() {
        var request = new CreditCardDto(null, null, null, null, null, null);
        webTestClient
                .post()
                .uri("/credit-cards")
                .headers(auth())
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.type")
                .isEqualTo("bad_request")
                .jsonPath("$.message")
                .value((String message) -> {
                    Assertions.assertInstanceOf(String.class, message);
                    Assertions.assertTrue(message.contains("customer is mandatory"));
                    Assertions.assertTrue(message.contains("number is mandatory"));
                    Assertions.assertTrue(message.contains("brand is mandatory"));
                });
    }

    @Test
    void testCreateAlreadyExistingCardReturnsConflict() {
        var request = new CreditCardDto(
                null,
                "customer_id",
                5000000000000001L,
                Brand.MASTERCARD,
                null,
                null);

        webTestClient
                .post()
                .uri("/credit-cards")
                .headers(auth())
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.type")
                .isEqualTo("business_rule")
                .jsonPath("$.message")
                .isEqualTo("There is already a registered card with the given number");
    }

    @Test
    void testValidCardRequestReturnsCreated() {
        var number = 5000000000000004L;
        var request = new CreditCardDto(null, "customer_id", number, Brand.MASTERCARD, null, null);

        webTestClient
                .post()
                .uri("/credit-cards")
                .headers(auth())
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CREATED);

        var card = creditCardRepository.findByNumber(number);
        Assertions.assertTrue(card.isPresent());
        creditCardRepository.delete(card.get());
    }

    @ParameterizedTest
    @EnumSource(Status.class)
    void testGetCardsByStatusRetunsCardPage(Status status) {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/credit-cards")
                        .queryParam("customer_id", "CUSTOMER_TEST_ID")
                        .queryParam("status", status.name())
                        .build())
                .headers(auth())
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody()
                .jsonPath("$.totalElements")
                .isEqualTo(1);
    }

    @Test
    void testGetCardWithRandomCustomerIdReturnsEmptyPage() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/credit-cards")
                        .queryParam("customer_id", "SOME_CUSTOMER_ID")
                        .build())
                .headers(auth())
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody()
                .jsonPath("$.totalElements")
                .isEqualTo(0);
    }

    @Test
    void testRemovalAttemptOnInactiveCardReturnsConflict() {
        webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path("/credit-cards/CARD_2").build())
                .headers(auth())
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Card with id CARD_2 is already inactive");
    }

    @Test
    void testRemovalAttemptOnActiveCardReturnsOk() {
        webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path("/credit-cards/CARD_3").build())
                .headers(auth())
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody()
                .jsonPath("$.status")
                .isEqualTo(Status.INACTIVE.name());
    }

    @Test
    void testRequestWithoutCredentialsReturnsUnauthorized() {
        webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path("/credit-cards/CARD_3").build())
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testGetCardWithExistingIdReturnsOk() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/credit-cards/CARD_3").build())
                .headers(auth())
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    void testGetCardWithNonExistingIdReturnsNotFound() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/credit-cards/CARD_8").build())
                .headers(auth())
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateRequestOnInactiveCardReturnsConflict() {
        var request = new CreditCardDto(null, "customer", 5000000000000002L, Brand.MASTERCARD, null, null);
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder.path("/credit-cards/CARD_2").build())
                .headers(auth())
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testUpdateRequestWithAlreadyUsedNumberReturnsConflict() {
        var request = new CreditCardDto(null, "customer", 5000000000000001L, Brand.VISA, null, null);
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder.path("/credit-cards/CARD_3").build())
                .headers(auth())
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT);
    }


    private Consumer<HttpHeaders> auth() {
        return httpHeaders -> httpHeaders.setBasicAuth("test_user", "test_password");
    }

}
