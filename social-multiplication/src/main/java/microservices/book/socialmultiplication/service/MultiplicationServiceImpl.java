package microservices.book.socialmultiplication.service;

import microservices.book.socialmultiplication.domain.MultiplicationResultAttempt;
import microservices.book.socialmultiplication.domain.User;
import microservices.book.socialmultiplication.event.EventDispatcher;
import microservices.book.socialmultiplication.event.MultiplicationSolvedEvent;
import microservices.book.socialmultiplication.repository.MultiplicationRepository;
import microservices.book.socialmultiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.socialmultiplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import microservices.book.socialmultiplication.domain.Multiplication;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {
    private final RandomGeneratorService randomGeneratorService;
    private final MultiplicationResultAttemptRepository multiplicationResultAttemptRepository;
    private final UserRepository userRepository;
    private final MultiplicationRepository multiplicationRepository;
    private final EventDispatcher eventDispatcher;

    @Autowired
    public MultiplicationServiceImpl(
            final RandomGeneratorService randomGeneratorService,
            final MultiplicationResultAttemptRepository resultAttemptRepository,
            final UserRepository userRepository,
            final MultiplicationRepository multiplicationRepository,
            final EventDispatcher eventDispatcher) {
        this.randomGeneratorService = randomGeneratorService;
        this.multiplicationResultAttemptRepository = resultAttemptRepository;
        this.userRepository = userRepository;
        this.multiplicationRepository = multiplicationRepository;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public Multiplication createRandomMultiplication() {
        int factorA = randomGeneratorService.generateRandomFactor();
        int factorB = randomGeneratorService.generateRandomFactor();
        return new Multiplication(factorA, factorB);
    }

    @Override
    public boolean checkMultiplicationAttempt(MultiplicationResultAttempt resultAttempt) {

        Assert.isTrue(!resultAttempt.isCorrect(), "Can't send an attempt marked as correct");

        Optional<User> user = userRepository.findByAlias(resultAttempt.getUser().getAlias());
        Optional<Multiplication> multiplication = multiplicationRepository.findByFactorAAndFactorB(
                resultAttempt.getMultiplication().getFactorA(),
                resultAttempt.getMultiplication().getFactorB());

        boolean isCorrect = resultAttempt.getResultAttempt() ==
                resultAttempt.getMultiplication().getFactorA() *
                        resultAttempt.getMultiplication().getFactorB();

        MultiplicationResultAttempt checkedAttempt = new MultiplicationResultAttempt(user.orElse(resultAttempt.getUser()),
                multiplication.orElse(resultAttempt.getMultiplication()),
                resultAttempt.getResultAttempt(),
                isCorrect);

        multiplicationResultAttemptRepository.save(checkedAttempt);
        this.eventDispatcher.send(new MultiplicationSolvedEvent(
                checkedAttempt.getId(), checkedAttempt.getUser().getId(), checkedAttempt.isCorrect()));
        return isCorrect;
    }

    @Override
    public List<MultiplicationResultAttempt> getStatsForUser(String userAlias) {
        return this.multiplicationResultAttemptRepository.findTop5ByUserAliasOrderByIdDesc(userAlias);
    }
}