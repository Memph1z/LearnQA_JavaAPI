package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestcase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String locale = "api_dev";

    @Test
    public void testGetUserDataNotAuth(){
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/" + locale + "/user/2")
                .andReturn();

        Assertions.assertJsonHasField(responseUserData,"username");
        Assertions.assertJsonHasNoField(responseUserData, "firstName");
        Assertions.assertJsonHasNoField(responseUserData, "lastName");
        Assertions.assertJsonHasNoField(responseUserData, "email");
        Assertions.assertResponseCodeEquals(responseUserData, 200);
    }

    @Test
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String,String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/" + locale + "/user/login")
                .andReturn();

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/" + locale + "/user/2")
                .andReturn();

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
        Assertions.assertResponseCodeEquals(responseUserData, 200);
    }
    @Test
    public void testGetUserDetailsAuthAsOtherUser(){
        Map<String,String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/" + locale + "/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/" + locale + "/user/3", header, cookie);
        String[] unexpectedFields = {"id", "firstName", "lastName", "email"};
        Assertions.assertJsonHasNoFields(responseUserData, unexpectedFields);
        Assertions.assertResponseCodeEquals(responseUserData, 200);
    }
}
