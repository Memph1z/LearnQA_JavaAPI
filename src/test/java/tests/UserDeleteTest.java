package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.BaseTestcase;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Deletion cases")
@Feature("Deletion")
public class UserDeleteTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String cookie;
    String header;
    String userId;
    String locale = "api_dev";

    @Test
    @Description("This test checks deleting undeletable user")
    @DisplayName("Test negative delete user")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("https://example.com/tests/n")
    public void testDeleteUndeletableUser(){
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseCheckAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/" + locale + "/user/login", authData);
        this.header = this.getHeader(responseCheckAuth, "x-csrf-token");
        this.cookie = this.getCookie(responseCheckAuth, "auth_sid");
        this.userId = responseCheckAuth.jsonPath().getString("user_id");

        //DELETE
        Response responseCheckDelete = apiCoreRequests.makeDeleteRequest("https://playground.learnqa.ru/" + locale + "/user/" + this.userId, this.header, this.cookie);
        Assertions.assertJsonByName(responseCheckDelete, "error", "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
        Assertions.assertResponseCodeEquals(responseCheckDelete, 400);
    }

    @Test
    @Description("This test checks deleting user successfully")
    @DisplayName("Test positive delete user")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("https://example.com/tests/n")
    public void testDeleteUser(){
        //GENERATE
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/" + locale + "/user/", userData);

        this.userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/" + locale + "/user/login", authData);
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");

        //DELETE
        Response responseCheckDelete = apiCoreRequests.makeDeleteRequest("https://playground.learnqa.ru/" + locale + "/user/" + this.userId, this.header, this.cookie);
        Assertions.assertJsonByName(responseCheckDelete, "success", "!");
        Assertions.assertResponseCodeEquals(responseCheckDelete, 200);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/" + locale + "/user/" + this.userId, this.header, this.cookie);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
        Assertions.assertResponseCodeEquals(responseUserData, 404);
    }

    @Test
    @Description("This test checks deleting user while sending token and cookie from other user")
    @DisplayName("Test negative delete user")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("https://example.com/tests/n")
    public void testDeleteOtherUser(){
        //GENERATE
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/" + locale + "/user/", userData);

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/" + locale + "/user/login", authData);
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");

        //DELETE
        Response responseCheckDelete = apiCoreRequests.makeDeleteRequest("https://playground.learnqa.ru/" + locale + "/user/11", this.header, this.cookie);
        Assertions.assertJsonByName(responseCheckDelete, "error", "This user can only delete their own account.");
        Assertions.assertResponseCodeEquals(responseCheckDelete, 400);
    }
}
