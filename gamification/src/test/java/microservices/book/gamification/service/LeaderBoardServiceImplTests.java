package microservices.book.gamification.service;

import microservices.book.gamification.domain.LeaderBoardRow;
import microservices.book.gamification.repository.BadgeCardRepository;
import microservices.book.gamification.repository.ScoreCardRepository;
import microservices.book.gamification.services.LeaderBoardService;
import microservices.book.gamification.services.LeaderBoardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LeaderBoardServiceImplTests {

    @Mock
    private ScoreCardRepository scoreCardRepository;

    private LeaderBoardServiceImpl leaderBoardServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.leaderBoardServiceImpl = new LeaderBoardServiceImpl(scoreCardRepository);
    }

    @Test
    public void checkGetCurrentLeaderBoard() throws Exception {

        List<LeaderBoardRow> leaderBoardRowList = Arrays.asList(
                new LeaderBoardRow(1L, 10),
                new LeaderBoardRow(2L, 20),
                new LeaderBoardRow(3L, 10),
                new LeaderBoardRow(4L, 20));

        // given
        given(scoreCardRepository.findFirst10()).willReturn(leaderBoardRowList);

        List<LeaderBoardRow> currentLeaderBoard = leaderBoardServiceImpl.getCurrentLeaderBoard();

        assertIterableEquals(leaderBoardRowList, currentLeaderBoard);
    }
}
