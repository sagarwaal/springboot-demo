package microservices.book.gamification.service;

import microservices.book.gamification.domain.Badge;
import microservices.book.gamification.domain.BadgeCard;
import microservices.book.gamification.domain.GameStats;
import microservices.book.gamification.domain.ScoreCard;
import microservices.book.gamification.repository.BadgeCardRepository;
import microservices.book.gamification.repository.ScoreCardRepository;
import microservices.book.gamification.services.GameService;
import microservices.book.gamification.services.GameServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class GameServiceImplTests {

    @Mock
    private ScoreCardRepository scoreCardRepository;

    @Mock
    private BadgeCardRepository badgeCardRepository;

    private GameService gameServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.gameServiceImpl = new GameServiceImpl(scoreCardRepository, badgeCardRepository);
    }

    @Test
    public void checkNewAttemptForUserFirstWonBadge() throws Exception{
        checkForBadge(1L, Badge.FIRST_WON, ScoreCard.DEFAULT_SCORE);
    }

    @Test
    public void checkNewAttemptForUserBronzeBadge() throws Exception{
        checkForBadge(1L, Badge.BRONZE_MULTIPLICATOR, 100);
    }

    @Test
    public void checkNewAttemptForUserSilverBadge() throws Exception{
        checkForBadge(1L, Badge.SILVER_MULTIPLICATOR, 150);
    }

    @Test
    public void checkNewAttemptForUserGoldBadge() throws Exception{
        checkForBadge(1L, Badge.GOLD_MULTIPLICATOR, 200);
    }

    @Test
    public void checkRetrieveStatsForUser() throws Exception{
        Long userId = 1L;
        int totalScore = 100;
        List<BadgeCard> badges = Arrays.asList(new BadgeCard(userId, Badge.BRONZE_MULTIPLICATOR));

        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId)).willReturn(badges);
        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);

        GameStats stats = gameServiceImpl.retrieveStatsForUser(userId);
        assertEquals(stats.getScore(), totalScore);
        assertEquals(badges.stream().map(BadgeCard::getBadge).collect(Collectors.toList()), stats.getBadges());
    }


    private void checkForBadge(Long userId, Badge badge, int scoreThreshold) throws Exception{
        List<ScoreCard> scoreCards = new ArrayList<>();

        int totalScore = 0;
        Long attemptId = 0L;
        while (totalScore < scoreThreshold) {
            ScoreCard card = new ScoreCard(userId, ++attemptId);
            totalScore += card.getScore();
            scoreCards.add(card);
        }

        assertTrue(scoreCards.size() > 0);

        ScoreCard lastScoreCard = scoreCards.get(scoreCards.size() - 1);

        List<BadgeCard> alreadyPresentBadges = new ArrayList<>();
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId)).willReturn(scoreCards);
        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId)).willReturn(alreadyPresentBadges);

        GameStats stats = gameServiceImpl.newAttemptForUser(userId, lastScoreCard.getAttemptId(), true);
        verify(scoreCardRepository).save(argThat((ScoreCard scoreCard) -> scoreCard.getScore() == lastScoreCard.getScore() && scoreCard.getUserId() == userId));

        assertEquals(lastScoreCard.getScore(), stats.getScore());
        assertTrue(stats.getBadges().stream().anyMatch((Badge currBadge) -> currBadge == badge));
    }
}
