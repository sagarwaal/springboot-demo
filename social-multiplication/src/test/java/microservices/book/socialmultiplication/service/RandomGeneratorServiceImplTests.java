package microservices.book.socialmultiplication.service;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomGeneratorServiceImplTests {
    
    private RandomGeneratorServiceImpl randomGeneratorServiceImpl;

    @BeforeEach
    public void setUp() {
        this.randomGeneratorServiceImpl = new RandomGeneratorServiceImpl();
    }

    @Test
    public void generateRandomFactorIsBetweenSpecifiedLimits() throws Exception {
        List<Integer> randomFactors =
                IntStream.range(0, 100).map(i -> randomGeneratorServiceImpl.generateRandomFactor())
                        .boxed().collect(Collectors.toList());
        
        randomFactors.stream().forEach(num -> assertTrue(num.intValue() >= 11 && num.intValue() <= 99));
    }
}
