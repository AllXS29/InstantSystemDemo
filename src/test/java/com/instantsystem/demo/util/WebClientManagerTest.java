package com.instantsystem.demo.util;

import com.instantsystem.demo.exception.RestCallException;
import com.instantsystem.demo.exception.UnexpectedHttpMethodException;
import com.instantsystem.demo.helper.ResourceConverter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class WebClientManagerTest {

    @Value("classpath:poitierParkingList.json")
    private Resource poitierParkingList;

    private MockWebServer mockWebServer;

    private String baseUrl;

    @BeforeEach
    void initialize() throws IOException {
        // Set up the mock web server
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
    }

    @AfterEach
    void clean() throws IOException {
        // Shutdown webServer
        mockWebServer.shutdown();
    }

    @Test
    public void makeHttpCall_get_shouldSucceed() {
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        WebClientManager manager = new WebClientManager();
        String response = manager.makeHttpCall(baseUrl, "get", "poitier");
        assertNotNull(response);
    }

    @Test
    public void makeHttpCall_getUpperCase_shouldSucceed() {
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        WebClientManager manager = new WebClientManager();
        String response = manager.makeHttpCall(baseUrl, "GET", "poitier");
        assertNotNull(response);
    }

    @Test
    public void makeHttpCall_getRandomCase_shouldSucceed() {
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        WebClientManager manager = new WebClientManager();
        String response = manager.makeHttpCall(baseUrl, "GeT", "poitier");
        assertNotNull(response);
    }

    @Test
    public void makeHttpCall_wrongMethod_shouldSucceed() {
        WebClientManager manager = new WebClientManager();
        UnexpectedHttpMethodException thrown = assertThrows(UnexpectedHttpMethodException.class, () -> {
            String response = manager.makeHttpCall(baseUrl, "whatMethod", "poitier");
        }, "UnexpectedHttpMethodException was expected");
    }

    @Test
    public void makeHttpCall_wrongUrl_shouldSucceed() {
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        WebClientManager manager = new WebClientManager();
        RestCallException thrown = assertThrows(RestCallException.class, () -> {
            String response = manager.makeHttpCall("www.someUrl-instantSystem.aze", "get", "poitier");
        }, "RestCallException was expected");
    }

    //TODO implement test for post, put, delete, patch but not used in the app and not developped for now, test ignored
}