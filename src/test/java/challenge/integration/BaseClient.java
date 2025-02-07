package challenge.integration;

import challenge.ChallengeApi;
import challenge.repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.function.Consumer;

@SpringBootTest(
        classes = ChallengeApi.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class BaseClient {

    @Autowired
    protected CreditCardRepository creditCardRepository;

    @Autowired
    protected WebTestClient webTestClient;

    protected Consumer<HttpHeaders> auth() {
        return httpHeaders -> httpHeaders.setBasicAuth("test_user", "test_password");
    }

}
