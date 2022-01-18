package microservices.book.socialmultiplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import microservices.book.socialmultiplication.domain.Multiplication;
import microservices.book.socialmultiplication.domain.MultiplicationResultAttempt;
import microservices.book.socialmultiplication.domain.User;
import microservices.book.socialmultiplication.service.MultiplicationService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MultiplicationResultAttemptController.class)
public class MultiplicationResultAttemptControllerTests {

    @MockBean
    private MultiplicationService multiplicationService;

    @Autowired
    private MockMvc mvc;

    private JacksonTester<MultiplicationResultAttempt> jsonResult;
    private JacksonTester<MultiplicationResultAttempt> jsonResponse;
    private JacksonTester<List<MultiplicationResultAttempt>> jsonResultAttemptList;

    @BeforeEach
    public void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void postResultReturnsCorrect() throws Exception {
        genericParameterizedTest(true);
    }

    @Test
    public void postResultReturnsIncorrect() throws Exception {
        genericParameterizedTest(false);
    }

    @Test
    public void getUserStats() throws Exception {
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("John");
        MultiplicationResultAttempt attempt1 = new MultiplicationResultAttempt(user, multiplication, 3010, false);
        MultiplicationResultAttempt attempt2 = new MultiplicationResultAttempt(user, multiplication, 3050, false);

        List<MultiplicationResultAttempt> attemptList = Lists.newArrayList(attempt1, attempt2);

        given(multiplicationService.getStatsForUser(user.getAlias())).willReturn(attemptList);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/results").param("alias", "John"))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus(), "Expected OK response code");
        assertEquals(jsonResultAttemptList.write(attemptList).getJson(), response.getContentAsString());
    }

    @Test
    public void getMultiplicationResultAttempt() throws Exception {
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("John");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3010, false);

        Long id = 1L;

        given(multiplicationService.getMultiplicationResultAttempt(id)).willReturn(attempt);

        String urlTemplate = String.format("/results/%s", id.toString());
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(urlTemplate))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jsonResult.write(attempt).getJson(), response.getContentAsString());
    }

    @Test
    public void getMultiplicationResultAttemptNotFound() throws Exception {

        Long id = 1L;
        given(multiplicationService.getMultiplicationResultAttempt(id)).willReturn(null);

        String urlTemplate = String.format("/results/%s", id.toString());
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(urlTemplate))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    void genericParameterizedTest(final boolean correct) throws Exception {
        given(multiplicationService.checkMultiplicationAttempt(any(MultiplicationResultAttempt.class))).willReturn(correct);

        User user = new User("John");
        Multiplication multiplication = new Multiplication(30, 50);
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3000, false);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post("/results")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonResult.write(attempt).getJson()))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus(), "Expected OK response code");
        assertEquals(jsonResponse.write(new MultiplicationResultAttempt(
                attempt.getUser(),
                attempt.getMultiplication(),
                attempt.getResultAttempt(),
                correct)).getJson(), response.getContentAsString());
    }
}
