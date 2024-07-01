package com.booking.tests;

import com.booking.utils.ApiUtils;
import com.booking.utils.ConfigLoader;
import com.booking.utils.TestDataStore;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.Map;

public class BookerApiTests {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = ConfigLoader.getEnv("BASE_URL");

        Map<String, String> credentials = ApiUtils.getCredentials();
        Response response = ApiUtils.authenticateAndGetToken(credentials);

        ApiUtils.ensureSuccessfulAuthentication(response);

        String token = ApiUtils.getTokenFromResponse(response);
        TestDataStore.storeData("token", token);
    }



    @Test(priority = 1)
    public void createBookingHappyPath() {
        String payload = ApiUtils.getHappyPathPayload();
        Response response = ApiUtils.postBooking(payload);
        System.out.println("The bookingid is:" + response.asString());

        Assert.assertEquals(response.statusCode(), 200);
       //as per 'bookerapi' documentation 'property:bookingid' is 'integer'
        String bookingId = String.valueOf(response.jsonPath().getInt("bookingid"));
        TestDataStore.storeData("bookingId", bookingId);
    }

    @Test(priority = 2)
    public void getBookingHappyPath() {
        String bookingId = TestDataStore.retrieveData("bookingId");
        Assert.assertNotNull(bookingId, "Booking ID is null!");

        Response response = ApiUtils.getBooking(bookingId);
        System.out.println("The getbooking details:" + response.asString());

        Assert.assertEquals(response.statusCode(), 200); //as per 'bookerapi' documentation
    }

    @Test(priority = 3)
    public void updateBookingHappyPath() {
        String bookingId = TestDataStore.retrieveData("bookingId");
        String token = TestDataStore.retrieveData("token");

        Assert.assertNotNull(bookingId, "Booking ID is null!");
        Assert.assertNotNull(token, "Token is null!");

        Response response = ApiUtils.updateBooking(bookingId, token);
        System.out.println("The updatedbooking details:" + response.asString());

        Assert.assertEquals(response.statusCode(), 200); //as per 'bookerapi' documentation
    }

    @Test(priority = 4)
    public void partialUpdateBookingHappyPath() {
        String bookingId = TestDataStore.retrieveData("bookingId");
        String token = TestDataStore.retrieveData("token");

        Assert.assertNotNull(bookingId, "Booking ID is null!");
        Assert.assertNotNull(token, "Token is null!");

        Response response = ApiUtils.partialUpdateBooking(bookingId, token);
        System.out.println("The partiallyupdatedbooking details:" + response.asString());

        Assert.assertEquals(response.statusCode(), 200); //as per 'bookerapi' documentation
    }

    @Test(priority = 5)
    public void deleteBookingHappyPath() {
        String bookingId = TestDataStore.retrieveData("bookingId");
        String token = TestDataStore.retrieveData("token");

        Assert.assertNotNull(bookingId, "Booking ID is null!");
        Assert.assertNotNull(token, "Token is null!");

        Response response = ApiUtils.deleteBooking(bookingId, token);
        System.out.println("The booking details deleted:" + response.asString());

        Assert.assertEquals(response.statusCode(), 201); //as per 'bookerapi' documentation
    }

    @Test(priority = 6)
    public void createBookingNegativePath() {
        String payload = ApiUtils.getIncompletePayload();
        Response response = ApiUtils.postBooking(payload);
        System.out.println("The invalidcreatebooking details:" + response.asString());

        Assert.assertNotEquals(response.statusCode(), 200);
    }

    @Test(priority = 7)
    public void getBookingNegativePath() {
        String nonExistentBookingId = "999999";
        Response response = ApiUtils.getBooking(nonExistentBookingId);
        System.out.println("The invalidgetbooking details:" + response.asString());

        Assert.assertNotEquals(response.statusCode(), 200);
    }

    @Test(priority = 8)
    public void deleteBookingNegativePath() {
        String nonExistentBookingId = "999999";
        String token = TestDataStore.retrieveData("token");
        Assert.assertNotNull(token, "Token is null!");

        Response response = ApiUtils.deleteBooking(nonExistentBookingId, token);
        System.out.println("The delete invalid booking details:" + response.asString());

        Assert.assertNotEquals(response.statusCode(), 201);
    }

    @AfterClass
    public void cleardata() {
        TestDataStore.cleanup();
        Assert.assertEquals(TestDataStore.cleanup(), 0, "TestDataStore should be empty after cleanup");
    }

}
