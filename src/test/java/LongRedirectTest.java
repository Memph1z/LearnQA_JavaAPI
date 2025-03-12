import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class LongRedirectTest {
    @Test
    public void testRestAssured(){
        String link = "https://playground.learnqa.ru/api/long_redirect";

        while (link != null){
            System.out.println("\n" + link);

            Response checkLocation = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(link)
                    .andReturn();
            link = checkLocation.getHeader("Location");
        }
    }
}
