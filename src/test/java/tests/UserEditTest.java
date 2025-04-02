package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestcase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Edit cases")
@Feature("Edit")
public class UserEditTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String locale = "api_dev";

    String cookie;
    String header;
    String userId;

    @BeforeEach
    public void generateAndLoginUser(){
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/" + locale + "/user/", userData);

        this.userId = responseCreateAuth.jsonPath().getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/" + locale + "/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
    }

    @Test
    @Description("This test checks editing user info successfully")
    @DisplayName("Test positive edit user")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("https://example.com/tests/n")
    public void testEditJustCreatedTest(){

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/" + locale + "/user/" + this.userId, this.header, this.cookie, editData);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/" + locale + "/user/" + this.userId, this.header, this.cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }
    @Test
    @Description("This test checks editing user info w/o sending auth token")
    @DisplayName("Test negative edit user")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("https://example.com/tests/n")
    public void testEditNoAuthToken(){
        //EDIT
        String newName = "Even More Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithCookie("https://playground.learnqa.ru/" + locale + "/user/2", this.cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "Auth token not supplied");
    }
    @Test
    @Description("This test checks editing user info w/o sending auth cookie")
    @DisplayName("Test negative edit user")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("https://example.com/tests/n")
    public void testEditNoAuthCookie(){
        //EDIT
        String newName = "Even More Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithToken("https://playground.learnqa.ru/" + locale + "/user/2", this.header, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "Auth token not supplied");
    }
    @Test
    @Description("This test checks editing user info while sending auth cookie or token from other user")
    @DisplayName("Test negative edit user")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("https://example.com/tests/n")
    public void testEditAuthAsOtherUser() {
        //EDIT
        String newName = "Even More Changed Name2";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/" + locale + "/user/120068", this.header, this.cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "This user can only edit their own data.");
    }
    @Test
    @Description("This test checks editing user info with irregular email")
    @DisplayName("Test negative edit user")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("https://example.com/tests/n")
    public void testEditUserWithIrregularEmail() {
        //EDIT
        Map<String, String> editData = new HashMap<>();
        editData.put("email", "2783648723648example.com");

        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/" + locale + "/user/" + this.userId, this.header, this.cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "Invalid email format");
    }
    @Test
    @Description("This test checks editing user info with short first name")
    @DisplayName("Test negative edit user")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("https://example.com/tests/n")
    public void testEditUserWithShortFirstName() {
        //EDIT
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "1");

        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/" + locale + "/user/" + this.userId, this.header, this.cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "The value for field `firstName` is too short");
    }
}
