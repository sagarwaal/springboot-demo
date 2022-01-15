package microservices.book.gamification.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class LeaderBoardRow {

    private final Long userId;
    private final long totalScore;

    public LeaderBoardRow(){
        this(null, 0);
    }
}
