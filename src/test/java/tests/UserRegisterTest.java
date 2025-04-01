package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestcase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import lib.ApiCoreRequests;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


public class UserRegisterTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();


    @Test
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";
        Map<String,String> userData = new HashMap<>();
        userData.put("email",email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();

        Assertions.assertResponseTextEquals(responseCreateAuth,"Users with email '" + email + "' already exists");
        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
    }
    @Test
    public void testCreateUserSuccessfully(){
        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }
    @Test
    public void testCreateUserWithIrregularEmail(){
        Map<String, String> faultyUserData = new HashMap<>();
        faultyUserData.put("email", "2783648723648example.com");
        faultyUserData = DataGenerator.getRegistrationData(faultyUserData);

        Response responseForCheck = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user",faultyUserData);
        Assertions.assertResponseCodeEquals(responseForCheck,400);
        Assertions.assertResponseTextEquals(responseForCheck, "Invalid email format");
    }
    @Test
    public void testCreateUserWithShortFirstName(){
        Map<String, String> faultyUserData = new HashMap<>();
        faultyUserData.put("firstName", "1");
        faultyUserData = DataGenerator.getRegistrationData(faultyUserData);

        Response responseForCheck = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user",faultyUserData);
        Assertions.assertResponseCodeEquals(responseForCheck,400);
        Assertions.assertResponseTextEquals(responseForCheck, "The value of 'firstName' field is too short");
    }
    @Test
    public void testCreateUserWithLongFirstName(){
        Map<String, String> faultyUserData = new HashMap<>();
        faultyUserData.put("firstName", "fihitjbxeegigugymfnjzfxcjeynrzerhaeictzarnkkenuvmfraeudjtmankcxguadzuuyueiwxrmaiiwvyeztwpecwmzkwimceimpaubxjpxfjzhvqmzdvjihetjaubzavmpqeykvzxicqzuifvzxtxfhnmixmrewcwupwtewvqmptgueaqmcbvfkgrrmrqcqivnubccdrnfmugaajcaavwcvnnivkwrgiaznhrhbjkbxnhkactfizxdj");
        faultyUserData = DataGenerator.getRegistrationData(faultyUserData);

        Response responseForCheck = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user",faultyUserData);
        Assertions.assertResponseCodeEquals(responseForCheck,400);
        Assertions.assertResponseTextEquals(responseForCheck, "The value of 'firstName' field is too long");
    }
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithMissingKeys(String condition){

        Response responseForCheck = apiCoreRequests.makePostRequestWithMissingKey("https://playground.learnqa.ru/api/user",condition);
        Assertions.assertResponseTextEquals(responseForCheck, "The following required params are missed: " + condition);
    }
}
