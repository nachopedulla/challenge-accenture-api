package challenge.repository;

import challenge.model.entity.CreditCard;
import challenge.model.enums.Status;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CreditCardRepository extends CrudRepository<CreditCard, String>, JpaSpecificationExecutor<CreditCard> {

    Optional<CreditCard> findByNumber(Long number);

}
