package microservices.book.gamification.services;

import lombok.extern.slf4j.Slf4j;
import microservices.book.gamification.client.MultiplicationResultAttemptClient;
import microservices.book.gamification.client.dto.MultiplicationResultAttempt;
import microservices.book.gamification.domain.Badge;
import microservices.book.gamification.domain.BadgeCard;
import microservices.book.gamification.domain.GameStats;
import microservices.book.gamification.domain.ScoreCard;
import microservices.book.gamification.repository.BadgeCardRepository;
import microservices.book.gamification.repository.ScoreCardRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GameServiceImpl implements GameService {

    private static final int LUCKY_NUMBER = 42;

    private ScoreCardRepository scoreCardRepository;
    private BadgeCardRepository badgeCardRepository;
    private MultiplicationResultAttemptClient multiplicationResultAttemptClient;

    public GameServiceImpl(
            ScoreCardRepository scoreCardRepository,
            BadgeCardRepository badgeCardRepository,
            MultiplicationResultAttemptClient multiplicationResultAttemptClient) {
        this.scoreCardRepository = scoreCardRepository;
        this.badgeCardRepository = badgeCardRepository;
        this.multiplicationResultAttemptClient = multiplicationResultAttemptClient;
    }

    @Override
    public GameStats newAttemptForUser(Long userId, Long attemptId, boolean correct) {
        if (correct) {
            ScoreCard scoreCard = new ScoreCard(userId, attemptId);
            scoreCardRepository.save(scoreCard);
            log.info("User with id {} scored {} points for attempt id {}", scoreCard.getUserId(), scoreCard.getScore(), scoreCard.getAttemptId());
            List<BadgeCard> badgeCards = processForBadges(userId, attemptId);
            return new GameStats(userId, scoreCard.getScore(), badgeCards.stream().map(BadgeCard::getBadge).collect(Collectors.toList()));
        }

        return GameStats.emptyStats(userId);
    }

    @Override
    public GameStats retrieveStatsForUser(Long userId) {
        int score = scoreCardRepository.getTotalScoreForUser(userId);
        List<BadgeCard> badgeCards = badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId);
        return new GameStats(userId, score, badgeCards.stream().map(BadgeCard::getBadge).collect(Collectors.toList()));
    }

    private List<BadgeCard> processForBadges(final Long userId, final Long attemptId) {
        List<BadgeCard> badgeCards = new ArrayList<>();

        int totalScore = scoreCardRepository.getTotalScoreForUser(userId);
        log.info("New score for user with id {} is {}", userId, totalScore);

        List<ScoreCard> scoreCardList = scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId);
        List<BadgeCard> badgeCardList = badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId);

        MultiplicationResultAttempt attempt = multiplicationResultAttemptClient.retrieveMultiplicationResultAttemptById(attemptId);

        if ((attempt.getMultiplicationFactorA() == LUCKY_NUMBER
                || attempt.getMultiplicationFactorB() == LUCKY_NUMBER)
                && !containsBadge(badgeCardList, Badge.LUCKY_NUMBER)) {
            BadgeCard luckyNumberBadge = giveBadgeToUser(Badge.LUCKY_NUMBER, userId);
            badgeCards.add(luckyNumberBadge);
        }

        checkAndGiveBadgeBasedOnScore(badgeCardList, Badge.BRONZE_MULTIPLICATOR, totalScore, 100, userId)
                .ifPresent(badgeCards::add);

        checkAndGiveBadgeBasedOnScore(badgeCardList, Badge.SILVER_MULTIPLICATOR, totalScore, 150, userId)
                .ifPresent(badgeCards::add);

        checkAndGiveBadgeBasedOnScore(badgeCardList, Badge.GOLD_MULTIPLICATOR, totalScore, 200, userId)
                .ifPresent(badgeCards::add);

        if (scoreCardList.size() == 1 && !containsBadge(badgeCardList, Badge.FIRST_WON)) {
            BadgeCard firstWon = giveBadgeToUser(Badge.FIRST_WON, userId);
            badgeCards.add(firstWon);
        }

        return badgeCards;
    }

    private Optional<BadgeCard> checkAndGiveBadgeBasedOnScore(
            final List<BadgeCard> badgeCards, final Badge badge, final int score, final int scoreThreshold, final Long userId) {

        if (score >= scoreThreshold &&
                !containsBadge(badgeCards, badge)) {
            return Optional.of(giveBadgeToUser(badge, userId));
        }

        return Optional.empty();
    }

    private BadgeCard giveBadgeToUser(Badge badge, Long userId) {
        BadgeCard badgeCard = new BadgeCard(userId, badge);
        badgeCardRepository.save(badgeCard);
        log.info("User with id {} won a new badge: {}", userId, badge);

        return badgeCard;
    }

    private boolean containsBadge(List<BadgeCard> badgeCards, Badge badge) {

        for (BadgeCard badgeCard : badgeCards) {
            if (badgeCard.getBadge() == badge) {
                return true;
            }
        }

        return false;
    }
}
