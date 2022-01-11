package microservices.book.socialmultiplication.service;

import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class RandomGeneratorServiceImpl implements RandomGeneratorService {

    private static final int MINIMUM_NUMBER = 11;
    private static final int MAXIMUM_NUMBER = 99;

    @Override
    public int generateRandomFactor() {
        return new Random().nextInt((MAXIMUM_NUMBER - MINIMUM_NUMBER) + 1) + MINIMUM_NUMBER;
    }
}
