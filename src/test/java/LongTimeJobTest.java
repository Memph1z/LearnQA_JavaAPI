import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class LongTimeJobTest {

    @Test
    public void testLongTimeJob() throws InterruptedException {
        String token;
        int sleepTimer;
        String status;
        String result;
            JsonPath noTokenGet = RestAssured
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();

            token = noTokenGet.get("token");
            sleepTimer = noTokenGet.get("seconds");
        System.out.println("Token is: " + token);

        JsonPath prematureTokenGet = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        prematureTokenGet.prettyPrint();
        status = prematureTokenGet.get("status");

        if (Objects.equals(status, "Job is NOT ready")) {
            Thread.sleep(sleepTimer * 1000L);
            JsonPath matureTokenGet = RestAssured
                    .given()
                    .queryParam("token", token)
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();
            status = matureTokenGet.get("status");
            result = matureTokenGet.get("result");
            if (Objects.equals(status, "Job is ready") && result != null) {
                matureTokenGet.prettyPrint();
            }
        }
    }
}
