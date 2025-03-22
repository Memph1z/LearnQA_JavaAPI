import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestcase;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CookieHomeworkTest  extends BaseTestcase {

    @Test
    public void testLongTimeJob() {
            Response response = RestAssured
                    .get("https://playground.learnqa.ru/ajax/api/homework_cookie")
                    .andReturn();
            String cookie = this.getCookie(response,"HomeWork");
        assertEquals("hw_value", cookie, "Cookie doesn't match");
    }
}
