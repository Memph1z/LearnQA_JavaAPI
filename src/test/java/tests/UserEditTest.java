package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestcase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    String cookie;
    String header;
    String userId;

    @BeforeEach
    public void generateAndLoginUser(){
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        this.userId = responseCreateAuth.jsonPath().getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
    }

    @Test
    public void testEditJustCreatedTest(){

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + this.userId, this.header, this.cookie, editData);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + this.userId, this.header, this.cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }
    @Test
    public void testEditNoAuthToken(){
        //EDIT
        String newName = "Even More Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithCookie("https://playground.learnqa.ru/api/user/2", this.cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "Auth token not supplied");
    }
    @Test
    public void testEditNoAuthCookie(){
        //EDIT
        String newName = "Even More Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithToken("https://playground.learnqa.ru/api/user/2", this.header, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "Auth token not supplied");
    }
    @Test
    public void testEditAuthAsOtherUser() {
        //EDIT
        String newName = "Even More Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/11", this.header, this.cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "This user can only edit their own data.");
    }
    @Test
    public void testEditUserWithIrregularEmail() {
        //EDIT
        Map<String, String> editData = new HashMap<>();
        editData.put("email", "2783648723648example.com");

        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + this.userId, this.header, this.cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "Invalid email format");
    }
    @Test
    public void testEditUserWithShortFirstName() {
        //EDIT
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "1");

        Response responseEditUser = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + this.userId, this.header, this.cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "The value for field `firstName` is too short");
    }
}
