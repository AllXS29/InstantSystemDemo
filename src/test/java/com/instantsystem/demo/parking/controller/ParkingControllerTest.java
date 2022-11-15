package com.instantsystem.demo.parking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ParkingControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ParkingManagerRepository parkingManagerRepository;

    @Value("classpath:poitierParkingList.json")
    private Resource poitierParkingList;

    @Value("classpath:poitierParkingList_oneEntry.json")
    private Resource poitierParkingListOneEntry;

    @Value("classpath:poitierParkingPlaces.json")
    private Resource poitierParkingPlaces;

    @Value("classpath:poitierParkingPlaces_oneEntry.json")
    private Resource poitierParkingPlacesOneEntry;

    private MockWebServer mockWebServer;

    private ParkingManager manager;

    private String baseUrl;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void initialize() throws IOException {
        // Set up the mock web server
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());

        // Clean DB just in case
        parkingManagerRepository.deleteAll();

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
    public void getParkings_noRange_shouldSucceed() throws Exception {
        // Mock Http call to distant URL
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        String s_poitierParkingPlaces = ResourceConverter.convertResourceToString(poitierParkingPlaces);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingPlaces));

        // Prepare compare object
        Parking parking = new Parking("GRAND CERF", "GRAND CERF", "Poitier", null, null, "Stationnement longue durée (Zone violet) - Pour les horaires 12H maximum application du FPS", new Position(46.58716073, 0.3382104));
        // Make request to our endpoint
        MvcResult mvcResult = mockMvc.perform(get("/parkings/city/Poitier")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        //Parse response
        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Parking[] parkingResponse = objectMapper.readValue(response, Parking[].class);
        //compare values
        assertEquals(parkingResponse.length, 28);
        assertTrue(parking.equals(parkingResponse[0]));
    }

    @Test
    public void getParkingsByName_shouldSucceed() throws Exception {
        // Mock Http call to distant URL
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingListOneEntry);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        String s_poitierParkingPlaces = ResourceConverter.convertResourceToString(poitierParkingPlacesOneEntry);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingPlaces));

        // Prepare compare object
        Parking parking = new Parking("PALAIS DE JUSTICE", "PALAIS DE JUSTICE", "Poitier", 70, 47, "Parking en enclos sous barri\u00e9res payant de 9h \u00e0 19h du lundi au samedi, gratuit dimanche et jours f\u00e9ri\u00e9s.", new Position(46.58595805, 0.35129543));
        // Make request to our endpoint
        MvcResult mvcResult = mockMvc.perform(get("/parkings/city/Poitier/name/PALAIS DE JUSTICE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        //Parse response
        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Parking parkingResponse = objectMapper.readValue(response, Parking.class);
        //compare values
        assertTrue(parking.equals(parkingResponse));
    }

    @Test
    public void getParkings_inRange_shouldSucceed() throws Exception {
        // Mock Http call to distant URL
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingListOneEntry);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        String s_poitierParkingPlaces = ResourceConverter.convertResourceToString(poitierParkingPlacesOneEntry);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingPlaces));

        // Prepare compare object
        Parking parking = new Parking("PALAIS DE JUSTICE", "PALAIS DE JUSTICE", "Poitier", 70, 47, "Parking en enclos sous barri\u00e9res payant de 9h \u00e0 19h du lundi au samedi, gratuit dimanche et jours f\u00e9ri\u00e9s.", new Position(46.58595805, 0.35129543));
        // Make request to our endpoint, position is the same as the parking and the range is 1 meter to be sure to have 1 results only
        MvcResult mvcResult = mockMvc.perform(get("/parkings/city/Poitier?lat=46.58595805&lon=0.35129543&range=0.001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        //Parse response
        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Parking[] parkingResponse = objectMapper.readValue(response, Parking[].class);
        //compare values
        assertTrue(parking.equals(parkingResponse[0]));
    }

    @Test
    public void getParkings_shouldFail_UrlNotReachable() throws Exception {
        manager.getRequestsData().get(0).setUrl("www.InstantSystem-False-url.aze");
        parkingManagerRepository.save(manager);
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        
        // Make request to our endpoint
        mockMvc.perform(get("/parkings/city/Poitier")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RestCallException))
                .andExpect(result -> assertEquals(result.getResolvedException().getMessage(),
    "Failed to reach the url : www.InstantSystem-False-url.aze, with method : get, for the city : Poitier"));
    }

    @Test
    public void getParkings_shouldFail_WrongHttpMethod() throws Exception {
        manager.getRequestsData().get(0).setMethod("whatMethod?");
        parkingManagerRepository.save(manager);

        // Make request to our endpoint
        mockMvc.perform(get("/parkings/city/Poitier")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(406))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UnexpectedHttpMethodException))
                .andExpect(result -> assertEquals(result.getResolvedException().getMessage(),
String.format("Failed to process the request for the city : Poitier, at url : %s, with method : whatmethod?." +
        "\nPlease contact administrator, configuration is wrong for this city endpoint", baseUrl)));
    }

    @Test
    public void getParkings_shouldFail_BodyParseError() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("This body is not a json, will trigger an Exception"));
        // Make request to our endpoint
        mockMvc.perform(get("/parkings/city/Poitier")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BuildParkingException))
                .andExpect(result -> assertEquals(result.getResolvedException().getMessage(),
                        "Failed to retrieve parkings for city Poitier"));
    }

    @Test
    public void getParkings_shouldFail_mapperError() throws Exception {
        manager.getRequestsData().get(0).getiSMapper().getiSFields().setName("fields.unknown");
        parkingManagerRepository.save(manager);
        String s_poitierParkingList = ResourceConverter.convertResourceToString(poitierParkingList);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(s_poitierParkingList));
        // Make request to our endpoint
        mockMvc.perform(get("/parkings/city/Poitier")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InstantSystemMapperException))
                .andExpect(result -> assertEquals(result.getResolvedException().getMessage(),
"Failed to retrieve parking for city Poitier\n We were not able to map the data, please contact your administrator."));
    }
}