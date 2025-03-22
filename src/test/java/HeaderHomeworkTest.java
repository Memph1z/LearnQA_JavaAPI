import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestcase;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeaderHomeworkTest extends BaseTestcase {

    @Test
    public void testLongTimeJob() {
            Response response = RestAssured
                    .get("https://playground.learnqa.ru/api/homework_header")
                    .andReturn();
        String header = response.getHeader("x-secret-homework-header");
        assertEquals("Some secret value",header, "Header doesn't match");
    }
}
