package microservices.book.gamification.repository;

import microservices.book.gamification.domain.LeaderBoardRow;
import microservices.book.gamification.domain.ScoreCard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Handles CRUD operations with ScoreCard
 */
public interface ScoreCardRepository extends CrudRepository<ScoreCard, Long> {

    /**
     * Gets total score for a user, by summing up all his ScoreCards
     * @param userId the id of the user for which total score should be retrieved
     * @return the total score of the given user
     */
    @Query("SELECT SUM(s.score) FROM microservices.book.gamification.domain.ScoreCard s WHERE s.userId = :userId GROUP BY s.userId")
    int getTotalScoreForUser(@Param("userId") final Long userId);

    /**
     * Retrieve a list of {@link LeaderBoardRow}s representing the LeaderBoard of user and their total score
     * @return the leader board, sorted by highest score first
     */
    @Query("SELECT NEW microservices.book.gamification.domain.LeaderBoardRow(s.userId, SUM(s.score)) FROM ScoreCard s GROUP BY s.userId ORDER BY SUM(s.score) DESC")
    List<LeaderBoardRow> findFirst10();

    /**
     * Retrieves all the {@link ScoreCard}s for a given user
     * @param userId the id of the user for which scores need to retrieved
     * @return a list containing all the ScoreCards for given user, sorted by most recent.
     */
    List<ScoreCard> findByUserIdOrderByScoreTimestampDesc(final Long userId);
}
