package microservices.book.socialmultiplication.service;

import microservices.book.socialmultiplication.domain.MultiplicationResultAttempt;
import microservices.book.socialmultiplication.domain.User;
import microservices.book.socialmultiplication.event.EventDispatcher;
import microservices.book.socialmultiplication.event.MultiplicationSolvedEvent;
import microservices.book.socialmultiplication.repository.MultiplicationRepository;
import microservices.book.socialmultiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.socialmultiplication.repository.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import microservices.book.socialmultiplication.domain.Multiplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.verify;

public class MultiplicationServiceImplTests {

    private MultiplicationServiceImpl multiplicationServiceImpl;

    @Mock
    private RandomGeneratorService randomGeneratorService;

    @Mock
    private MultiplicationResultAttemptRepository multiplicationResultAttemptRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultiplicationRepository multiplicationRepository;

    @Mock
    private EventDispatcher eventDispatcher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.multiplicationServiceImpl = new MultiplicationServiceImpl(
                randomGeneratorService, multiplicationResultAttemptRepository, userRepository, multiplicationRepository, eventDispatcher);
    }

    @Test
    public void checkCorrectAttemptTest() {
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("John");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3000, false);
        MultiplicationResultAttempt verifiedAttempt = new MultiplicationResultAttempt(attempt.getUser(), attempt.getMultiplication(), attempt.getResultAttempt(), true);
        MultiplicationSolvedEvent event = new MultiplicationSolvedEvent(
                attempt.getId(),
                attempt.getUser().getId(),
                true);

        given(userRepository.findByAlias("John")).willReturn(Optional.empty());
        given(multiplicationRepository.findByFactorAAndFactorB(50, 60)).willReturn(Optional.empty());

        boolean result = multiplicationServiceImpl.checkMultiplicationAttempt(attempt);
        assertTrue(result);

        verify(multiplicationResultAttemptRepository).save(verifiedAttempt);
        verify(eventDispatcher).send(eq(event));
    }

    @Test
    public void checkWrongAttemptTest() {
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("John");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 1000, false);
        MultiplicationResultAttempt verifiedAttempt = new MultiplicationResultAttempt(attempt.getUser(), attempt.getMultiplication(), attempt.getResultAttempt(), false);
        MultiplicationSolvedEvent event = new MultiplicationSolvedEvent(
                attempt.getId(),
                attempt.getUser().getId(),
                false);

        given(userRepository.findByAlias("John")).willReturn(Optional.empty());
        given(multiplicationRepository.findByFactorAAndFactorB(50, 60)).willReturn(Optional.empty());

        boolean result = multiplicationServiceImpl.checkMultiplicationAttempt(attempt);
        assertFalse(result);

        verify(multiplicationResultAttemptRepository).save(verifiedAttempt);
        verify(eventDispatcher).send(eq(event));
    }

    @Test
    public void retrieveStatsTest() {
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("John");
        MultiplicationResultAttempt attempt1 = new MultiplicationResultAttempt(user, multiplication, 3010, false);
        MultiplicationResultAttempt attempt2 = new MultiplicationResultAttempt(user, multiplication, 3010, false);

        List<MultiplicationResultAttempt> attemptList = Lists.newArrayList(attempt1, attempt2);

        given(userRepository.findByAlias("John")).willReturn(Optional.empty());
        given(multiplicationRepository.findByFactorAAndFactorB(50, 60)).willReturn(Optional.empty());

        given(multiplicationResultAttemptRepository.findTop5ByUserAliasOrderByIdDesc("John")).willReturn(attemptList);

        List<MultiplicationResultAttempt> latestAttempts = multiplicationServiceImpl.getStatsForUser("John");

        assertIterableEquals(attemptList, latestAttempts);
    }

    @Test
    public void getMultiplicationResultAttempt() {
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("John");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3010, false);

        Long id = 1L;
        given(multiplicationResultAttemptRepository.findById(id)).willReturn(Optional.of(attempt));

        MultiplicationResultAttempt resultAttempt = multiplicationServiceImpl.getMultiplicationResultAttempt(id);

        assertEquals(attempt, resultAttempt);
    }
}
