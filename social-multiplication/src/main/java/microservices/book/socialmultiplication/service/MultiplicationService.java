package microservices.book.socialmultiplication.service;

import microservices.book.socialmultiplication.domain.Multiplication;
import microservices.book.socialmultiplication.domain.MultiplicationResultAttempt;

import java.util.List;

public interface MultiplicationService {
    /**
     * Generates a {@link Multiplication} object
     *
     * @return a {@link Multiplication} of randomly generated numbers
     */
    Multiplication createRandomMultiplication();


    /**
     * @param resultAttempt {@link MultiplicationResultAttempt}
     * @return true if attempt matches the result of the multiplication, otherwise returns false
     */
    boolean checkMultiplicationAttempt(final MultiplicationResultAttempt resultAttempt);

    /**
     * @param userAlias
     * @return list of 5 attempts of user
     */
    List<MultiplicationResultAttempt> getStatsForUser(String userAlias);

    /**
     * @param id of the {@link MultiplicationResultAttempt}
     * @return {@link MultiplicationResultAttempt}
     */
    MultiplicationResultAttempt getMultiplicationResultAttempt(final Long id);
}