import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;


public class GetSecretTest {
    @Test
    public void testRestAssured() throws FileNotFoundException {
        Map<String, String> data = new HashMap<>();
        data.put("login", "super_admin");
        String response = "You are NOT authorized";
        String password = "";
        File file = new File("src/main/resources/passwords.txt");
        Scanner scanner = new Scanner(file);
        scanner.useDelimiter("\n");

        while (Objects.equals(response, "You are NOT authorized") && scanner.hasNext()) {
            password = scanner.next();
            data.put("password", password);
            Response responseForPost = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/api/get_secret_password_homework")
                    .andReturn();

            String responseCookie = responseForPost.getCookie("auth_cookie");

            Map<String, String> cookies = new HashMap<>();
            cookies.put("auth_cookie", responseCookie);

            Response responseForCheck = RestAssured
                    .given()
                    .body(data)
                    .cookies(cookies)
                    .when()
                    .post("https://playground.learnqa.ru/api/check_auth_cookie")
                    .andReturn();
            response = responseForCheck.getBody().asString();
        }
        System.out.println("\n" + response);
        System.out.println("\n" + password);
    }
}
