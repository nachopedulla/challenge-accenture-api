package challenge.model.entity;

import challenge.model.enums.Brand;
import challenge.model.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "CREDIT_CARDS",
        indexes = {
                @Index(columnList = "CARD_NUMBER", unique = true),
                @Index(columnList = "CUSTOMER_ID")
        }
)
public class CreditCard {

    @Id
    private String id;

    /* si bien es unique no se utiliza como id porque es dato sensible, deberia almacenarse encrypted */
    @Column(name = "CARD_NUMBER")
    private Long number;

    @Column(name = "CUSTOMER_ID")
    private String customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "BRAND")
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

    /* podria usarse spring audit */
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "LAST_MODIFIED_DATE")
    private LocalDateTime lastModifiedDate;

}
