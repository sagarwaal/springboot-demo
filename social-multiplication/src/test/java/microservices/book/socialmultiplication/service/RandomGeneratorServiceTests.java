package microservices.book.socialmultiplication.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RandomGeneratorServiceTests {

    @Autowired
    private RandomGeneratorService randomGeneratorService;

    @Test
    public void generateRandomFactorIsBetweenSpecifiedLimits() throws Exception {
        List<Integer> randomFactors =
                IntStream.range(0, 100).map(i -> randomGeneratorService.generateRandomFactor())
                        .boxed().collect(Collectors.toList());

        randomFactors.stream().forEach(num -> assertTrue(num.intValue() >= 11 && num.intValue() <= 99));
    }
}
