package challenge.controller;

import challenge.mapper.CreditCardDtoMapper;
import challenge.model.dto.CreditCardDto;
import challenge.model.dto.criteria.CreditCardCriteriaDto;
import challenge.model.enums.Status;
import challenge.service.CreditCardService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CreditCardController {

    private final CreditCardService creditCardService;

    @GetMapping("/credit-cards")
    public Page<CreditCardDto> getCards(
            @RequestParam("customer_id") String customer,
            @RequestParam(value = "status", required = false) Status status,
            Pageable pageable
    ) {
        var criteria = new CreditCardCriteriaDto(customer, status);
        var cards = creditCardService.getCardsByCriteria(criteria, pageable);

        return new PageImpl<>(
                cards.map(CreditCardDtoMapper::map).toList(),
                pageable,
                cards.getTotalElements()
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/credit-cards")
    private CreditCardDto create(@RequestBody @Valid CreditCardDto request) {
        return CreditCardDtoMapper.map(creditCardService.create(request));
    }

    @GetMapping("/credit-cards/{id}")
    private CreditCardDto getCard(@PathVariable String id) {
        return CreditCardDtoMapper.map(creditCardService.getById(id));
    }

    @PutMapping("/credit-cards/{id}")
    private CreditCardDto updateCard(@PathVariable String id, @RequestBody @Valid CreditCardDto request) {
        return CreditCardDtoMapper.map(creditCardService.update(id, request));
    }

    @DeleteMapping("/credit-cards/{id}")
    private CreditCardDto remove(@PathVariable String id) {
        return CreditCardDtoMapper.map(creditCardService.remove(id));
    }

}
