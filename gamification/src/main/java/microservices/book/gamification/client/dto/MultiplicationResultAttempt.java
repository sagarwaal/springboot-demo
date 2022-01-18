package microservices.book.gamification.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@JsonDeserialize()
public final class MultiplicationResultAttempt {

    private final String userAlias;

    private final int multiplicationFactorA;
    private final int multiplicationFactorB;
    private final int resultAttempt;

    private final boolean correct;
}
