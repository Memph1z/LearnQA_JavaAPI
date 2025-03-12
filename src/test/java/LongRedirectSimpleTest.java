import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class LongRedirectSimpleTest {
    @Test
    public void testRestAssured(){


            Response checkLocation = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get("https://playground.learnqa.ru/api/long_redirect")
                    .andReturn();
            String link = checkLocation.getHeader("Location");
        System.out.println(link);
    }
}
