package microservices.book.gamification.domain;

/**
 * Enum with different type of badges that user can win
 */
public enum Badge {

    // Badges depending on score
    BRONZE_MULTIPLICATOR,
    SILVER_MULTIPLICATOR,
    GOLD_MULTIPLICATOR,

    // Badges won under some condition
    FIRST_ATTEMPT,
    FIRST_WON,
}
