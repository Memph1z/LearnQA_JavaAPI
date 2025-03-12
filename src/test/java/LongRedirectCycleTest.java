import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class LongRedirectCycleTest {
    @Test
    public void testRestAssured(){
        String link = "https://playground.learnqa.ru/api/long_redirect";
        int statusCode = 0;

        while (statusCode != 200 && link != null){
            Response checkLocation = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(link)
                    .andReturn();
            link = checkLocation.getHeader("Location");
            statusCode = checkLocation.getStatusCode();
        }
    }
}
