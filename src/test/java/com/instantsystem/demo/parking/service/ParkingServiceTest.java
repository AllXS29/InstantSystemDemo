package com.instantsystem.demo.parking.service;

import com.instantsystem.demo.exception.RestCallException;
import com.instantsystem.demo.exception.UnexpectedHttpMethodException;
import com.instantsystem.demo.helper.ResourceConverter;
import com.instantsystem.demo.parking.entity.*;
import com.instantsystem.demo.parking.exception.BuildParkingException;
import com.instantsystem.demo.parking.exception.InstantSystemMapperException;
import com.instantsystem.demo.parking.repository.ParkingManagerRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParkingServiceTest {

    @Autowired
    private ParkingManagerRepository parkingManagerRepository;

    @Autowired
    private ParkingService parkingService;

    @Value("classpath:poitierParkingList.json")
    private Resource poitierParkingList;

    @Value("classpath:poitierParkingPlaces.json")
    private Resource poitierParkingPlaces;

    private MockWebServer mockWebServer;

    private ParkingManager manager;

    @BeforeEach
    void initialize() throws IOException {
        // Set up the mock web server
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Clean DB just in case
        parkingManagerRepository.deleteAll();

        // Build test objects
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());

        // Populate DB
        ISFields isFields1 = new ISFields("fields.nom", "fields.nom", null, null, null, "fields.info", "fields.ylat", "fields.xlong");
        ISFields isFields2 = new ISFields("fields.nom", "fields.nom", null, "fields.capacite", "fields.places_restantes", null, null, null);

        ISMapper isMapper1 = new ISMapper("JSONObject", "records", isFields1);
        ISMapper isMapper2 = new ISMapper("JSONObject", "records", isFields2);

        RequestData requestData1 = new RequestData("get", baseUrl, null, isMapper1);
        RequestData requestData2 = new RequestData("get", baseUrl, null, isMapper2);

        List<RequestData> requestsData = new ArrayList<>();
        requestsData.add(requestData1);
        requestsData.add(requestData2);

        manager = new ParkingManager("Poitier", requestsData);
        manager = parkingManagerRepository.insert(manager);
    }

    @AfterEach
    void clean() throws IOException {
        // Shutdown webServer
        mockWebServer.shutdown();
    }

    @Test
    public void getParkings_noRange_shouldSucceed() {
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        String s_poitierParkingPlaces = ResourceConverter.convertResourceToString(poitierParkingPlaces);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingPlaces));
        List<Parking> parkings = parkingService.getParkings("Poitier");
        assertEquals(parkings.size(), 28);
    }

    @Test
    public void getParkingsByName_shouldSucceed() {
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        String s_poitierParkingPlaces = ResourceConverter.convertResourceToString(poitierParkingPlaces);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingPlaces));
        Parking parking = new Parking("PALAIS DE JUSTICE", "PALAIS DE JUSTICE", "Poitier", 70, 47, "Parking en enclos sous barri\u00e9res payant de 9h \u00e0 19h du lundi au samedi, gratuit dimanche et jours f\u00e9ri\u00e9s.", new Position(46.58595805, 0.35129543));
        Parking parkingFromRequest = parkingService.getParking("Poitier", "PALAIS DE JUSTICE");
        assertEquals(parking,parkingFromRequest);
    }

    @Test
    public void getParkings_inRange_shouldSucceed() {
        Position position = new Position(46.58595805, 0.35129543);
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        String s_poitierParkingPlaces = ResourceConverter.convertResourceToString(poitierParkingPlaces);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingPlaces));
        List<Parking> parkings = parkingService.getParkings("Poitier", position.getLatitude(), position.getLongitude(), 0.5);
        assertEquals(parkings.size(), 5);
    }

    @Test
    public void getParkings_shouldFail_UrlNotReachable() {
        manager.getRequestsData().get(0).setUrl("www.InstantSystem-False-url.aze");
        parkingManagerRepository.save(manager);
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        RestCallException thrown = assertThrows(RestCallException.class, () -> {
            List<Parking> parkings = parkingService.getParkings("Poitier");
        }, "RestCallException was expected");
    }

    @Test
    public void getParkings_shouldFail_WrongHttpMethod() {
        manager.getRequestsData().get(0).setMethod("whatMethod?");
        parkingManagerRepository.save(manager);
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        UnexpectedHttpMethodException thrown = assertThrows(UnexpectedHttpMethodException.class, () -> {
            List<Parking> parkings = parkingService.getParkings("Poitier");
        }, "UnexpectedHttpMethodException was expected");
    }

    @Test
    public void getParkings_shouldFail_BodyParseError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("This body is not a json, will trigger an Exception"));
        BuildParkingException thrown = assertThrows(BuildParkingException.class, () -> {
            List<Parking> parkings = parkingService.getParkings("Poitier");
        }, "BuildParkingException was expected");
    }

    @Test
    public void getParkings_shouldFail_mapperError() {
        manager.getRequestsData().get(0).getiSMapper().getiSFields().setName("fields.unknown");
        parkingManagerRepository.save(manager);
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        InstantSystemMapperException thrown = assertThrows(InstantSystemMapperException.class, () -> {
            List<Parking> parkings = parkingService.getParkings("Poitier");
        }, "InstantSystemMapperException was expected");
    }
}