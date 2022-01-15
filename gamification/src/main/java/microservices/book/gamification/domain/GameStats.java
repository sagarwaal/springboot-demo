package microservices.book.gamification.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This object represents the result of one or more iterations of the game
 * It may contain any combinations of {@link ScoreCard} objects and {@link BadgeCard} objects
 * It can be used as delta or to represent the total amount of score/badges
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class GameStats {

    private final Long userId;
    private final int score;

    private final List<Badge> badges;

    public GameStats(){
        this(null, 0, new ArrayList<>());
    }

    /**
     * Factory  method to create an empty instance of GameStats for a user
     * Zero Points and No badges
     * @param userId
     * @return {@link GameStats} object with 0 score and no badges
     */
    public static GameStats emptyStats(Long userId){
        return new GameStats(userId, 0, Collections.emptyList());
    }

    /**
     * @return an unmodifiable view of the badge cards list
     */
    public List<Badge> getBadges(){
        return Collections.unmodifiableList(badges);
    }

}
