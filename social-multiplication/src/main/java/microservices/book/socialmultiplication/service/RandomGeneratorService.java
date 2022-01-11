package microservices.book.socialmultiplication.service;


/**
 * Service to generate random number
 */
public interface RandomGeneratorService {
    /**
     * @return randomly generated number between 11-99
     */
    int generateRandomFactor();
}
