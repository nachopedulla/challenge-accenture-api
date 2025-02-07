package challenge.controller;

import challenge.model.dto.CreditCardDto;
import challenge.model.dto.criteria.CreditCardCriteriaDto;
import challenge.model.enums.Status;
import challenge.service.InternalCallService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class InternalCallController {

    private final InternalCallService internalCallService;

    @GetMapping("/internal-credit-cards")
    public Page<CreditCardDto> getCardsByInternalCall(
            @RequestParam("customer_id") String customer,
            @RequestParam(value = "status", required = false) Status status,
            Pageable pageable
    ) {
        var criteria = new CreditCardCriteriaDto(customer, status);
        return internalCallService.getCards(criteria, pageable);
    }
}
