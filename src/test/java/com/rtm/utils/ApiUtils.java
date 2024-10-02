package com.rtm.utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ApiUtils {
    // Utility methods for beforeclass setup
    public static Map<String, String> getCredentials() {
        Properties props = new Properties();

        try (InputStream input = ApiUtils.class.getResourceAsStream("/config.properties")) {
            if (input == null) {
                throw new RuntimeException("Failed to find config.properties on the classpath");
            }
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to load the config.properties file");
        }

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", props.getProperty("username"));
        credentials.put("password", props.getProperty("password"));
        return credentials;
    }


    public static Response authenticateAndGetToken(Map<String, String> credentials) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .post("/auth");
    }

    public static void ensureSuccessfulAuthentication(Response response) {
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to authenticate and obtain the token.");
        }
    }

    public static String getTokenFromResponse(Response response) {
        return response.jsonPath().getString("token");
    }

    // Utility methods for constructing payloads
    public static String getHappyPathPayload() {
        return "{"
                + "\"firstname\": \"Ruuhi\","
                + "\"lastname\": \"Wahii\","
                + "\"totalprice\": 111,"
                + "\"depositpaid\": true,"
                + "\"bookingdates\": {\"checkin\": \"2023-08-15\",\"checkout\": \"2023-08-20\"},"
                + "\"additionalneeds\": \"Breakfast\""
                + "}";
    }

    public static String getUpdatedPayload() {
        return "{"
                + "\"firstname\": \"UpdatedName\","
                + "\"lastname\": \"UpdatedSurname\","
                + "\"totalprice\": 123,"
                + "\"depositpaid\": true,"
                + "\"bookingdates\": {\"checkin\": \"2023-08-15\",\"checkout\": \"2023-08-20\"},"
                + "\"additionalneeds\": \"Breakfast\""
                + "}";
    }

    public static String getPartialUpdatePayload() {
        return "{"
                + "\"firstname\": \"PartialUpdateName\""
                + "}";
    }

    public static String getIncompletePayload() {
        return "{"
                + "\"firstname\": \"Jim\""
                + "}";
    }

    // Utility methods for making API requests
    public static Response postBooking(String payload) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON) // Sets "Content-Type: application/json"
                .header("Accept", "application/json")  // Optionally set "Accept" header
                .body(payload)
                .post("/" + "booking");
    }

    public static Response getBooking(String bookingId) {
        return RestAssured
                .given()
                .header("Accept", "application/json")  // Optionally set "Accept" header
                .get("/" + "booking" + "/" +  bookingId);
    }

    public static Response updateBooking(String bookingId, String token) {
        String updatedPayload = getUpdatedPayload();

        return RestAssured
                .given()
                .header("Cookie", "token=" + token) // added token as header
                .header("Accept", "application/json")  // Optionally set "Accept" header
                .contentType(ContentType.JSON)
                .body(updatedPayload)
                .put("/" + "booking" + "/" + bookingId);
    }

    public static Response partialUpdateBooking(String bookingId, String token) {
        String partialPayload = getPartialUpdatePayload();

        return RestAssured
                .given()
                .header("Cookie", "token=" + token) // added token as header
                .header("Accept", "application/json")  // Optionally set "Accept" header
                .contentType(ContentType.JSON)
                .body(partialPayload)
                .patch("/" + "booking" + "/" +bookingId);
    }


    public static Response deleteBooking(String bookingId, String token) {
        return RestAssured
                .given()
                .header("Cookie", "token=" + token) // added token as header
                .header("Accept", "application/json")  // Optionally set "Accept" header
                .delete("/" + "booking" + "/" + bookingId);
    }
}
