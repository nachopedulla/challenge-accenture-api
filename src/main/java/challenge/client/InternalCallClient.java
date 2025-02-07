package challenge.client;

import challenge.client.exception.InternalCallClientException;
import challenge.client.model.PageResponseDto;
import challenge.model.dto.CreditCardDto;
import challenge.model.dto.criteria.CreditCardCriteriaDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class InternalCallClient {

    private final WebClient webClient;
    private final String password;
    private final String username;

    public InternalCallClient(
            @Value("${clients.internal-call.url}") String url,
            @Value("${clients.internal-call.username}") String username,
            @Value("${clients.internal-call.password}") String password
    ) {
        this.webClient = WebClient.builder().baseUrl(url).build();
        this.username = username;
        this.password = password;
    }

    public PageResponseDto<CreditCardDto> getCards(CreditCardCriteriaDto criteria, Pageable pageable) {
        /* ver de configurar timeout y manejo de errores */
        var type = new ParameterizedTypeReference<PageResponseDto<CreditCardDto>>() {};
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/credit-cards")
                        .queryParams(params(criteria, pageable))
                        .build())
                .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class).flatMap(error -> {
                    log.error("Error on credit-card-api call: status=[{}] response=[{}]", response.statusCode(), error);
                    return Mono.error(new InternalCallClientException("Error on internal api client call"));
                }))
                .bodyToMono(type)
                .block();
    }

    private MultiValueMap<String, String> params(CreditCardCriteriaDto criteria, Pageable pageable) {
        var params = new LinkedMultiValueMap<String, String>();
        params.add("customer_id", criteria.customer());

        if (criteria.status() != null) {
            params.add("status", criteria.status().name());
        }

        params.add("page", String.valueOf(pageable.getPageNumber()));
        params.add("size", String.valueOf(pageable.getPageSize()));

        if (pageable.getSort().isSorted()) {
            params.add("sort", pageable.getSort().toString().replace(": ", ","));
        }

        return params;
    }
}