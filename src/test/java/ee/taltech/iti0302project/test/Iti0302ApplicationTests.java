package ee.taltech.iti0302project.test;

import ee.taltech.iti0302project.Iti0302Application;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Iti0302Application.class)
class Iti0302ApplicationTests {

    @Test
    void contextLoads() {
        assertTrue(true);
    }

}
