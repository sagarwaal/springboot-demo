package microservices.book.socialmultiplication.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;


@RequiredArgsConstructor    
@Getter    
@ToString    
@EqualsAndHashCode
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"factorA", "factorB"}))
@Entity
public final class Multiplication {

    @Id
    @GeneratedValue
    @Column(name = "MULTIPLICATION_ID")
    private Long id;

    private final int factorA;
    private final int factorB;

    Multiplication() {
        this(0, 0);
    }
}