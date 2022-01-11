package microservices.book.socialmultiplication.repository;

import microservices.book.socialmultiplication.domain.Multiplication;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MultiplicationRepository extends CrudRepository<Multiplication, Long> {

    public Optional<Multiplication> findByFactorAAndFactorB(final int factorA, final int factorB);

}
