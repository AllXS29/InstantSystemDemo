package com.instantsystem.demo.parking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instantsystem.demo.parking.entity.ISFields;
import com.instantsystem.demo.parking.entity.ISMapper;
import com.instantsystem.demo.parking.entity.ParkingManager;
import com.instantsystem.demo.parking.entity.RequestData;
import com.instantsystem.demo.parking.exception.AlreadyExistingParkingManagerException;
import com.instantsystem.demo.parking.exception.NonExistingParkingManagerException;
import com.instantsystem.demo.parking.repository.ParkingManagerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ParkingManagerControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    private ParkingManagerRepository parkingManagerRepository;
    
    private ParkingManager manager;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Clean DB just in case
        parkingManagerRepository.deleteAll();

        // Populate DB
        ISFields isFields1 = new ISFields("fields.nom", "fields.nom", null, null, null, "fields.info", "fields.ylat", "fields.xlong");
        ISFields isFields2 = new ISFields("fields.nom", "fields.nom", null, "fields.capacite", "fields.places_restantes", null, null, null);

        ISMapper isMapper1 = new ISMapper("JSONObject", "records", isFields1);
        ISMapper isMapper2 = new ISMapper("JSONObject", "records", isFields2);

        RequestData requestData1 = new RequestData("get", "www.someUrl.com", null, isMapper1);
        RequestData requestData2 = new RequestData("get", "www.someUrl.com", null, isMapper2);

        List<RequestData> requestsData = new ArrayList<>();
        requestsData.add(requestData1);
        requestsData.add(requestData2);

        manager = new ParkingManager("Poitier", requestsData);
        manager = parkingManagerRepository.insert(manager);
    }

    @Test
    public void getAll_shouldSucceed() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/parking-manager")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        ParkingManager[] parkingManagersFromDB = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ParkingManager[].class);
        assertNotNull(parkingManagersFromDB);
        assertEquals(parkingManagersFromDB.length, 1);
        assertEquals(manager, parkingManagersFromDB[0]);
    }
    
    @Test
    public void getByCity_shouldSucceed() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/parking-manager/city/Poitier")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        ParkingManager parkingManagerFromDB = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ParkingManager.class);
        assertNotNull(parkingManagerFromDB);
        assertTrue(manager.equals(parkingManagerFromDB));
    }

    @Test
    public void getByCity_cityDoesntExists_shouldReturnNull() throws Exception {
        mockMvc.perform(get("/parking-manager/city/Sophia-antipolis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NonExistingParkingManagerException))
                .andExpect(result -> assertEquals(result.getResolvedException().getMessage(), "No parking manager found for city Sophia-antipolis"));
    }

    @Test
    public void createParkingManager_shouldSucceed() throws Exception {
        ISFields isFields1 = new ISFields("fields.nom", "fields.nom", null, null, null, "fields.info", "fields.ylat", "fields.xlong");
        ISFields isFields2 = new ISFields("fields.nom", "fields.nom", null, "fields.capacite", "fields.places_restantes", null, null, null);

        ISMapper isMapper1 = new ISMapper("JSONObject", "records", isFields1);
        ISMapper isMapper2 = new ISMapper("JSONObject", "records", isFields2);

        RequestData requestData1 = new RequestData("get", "www.someUrl.com", null, isMapper1);
        RequestData requestData2 = new RequestData("get", "www.someUrl.com", null, isMapper2);

        List<RequestData> requestsData = new ArrayList<>();
        requestsData.add(requestData1);
        requestsData.add(requestData2);

        ParkingManager parkingManager = new ParkingManager("Sophia-antipolis", requestsData);
        MvcResult mvcResult = mockMvc.perform(put("/parking-manager/city")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingManager)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        ParkingManager parkingManagerFromDB = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ParkingManager.class);
        assertNotNull(parkingManagerFromDB);
        parkingManager.setId(parkingManagerFromDB.getId());
        assertTrue(parkingManager.equals(parkingManagerFromDB));
    }

    @Test
    public void createParkingManager_cityAlreadySet_shouldFail() throws Exception {
        ISFields isFields1 = new ISFields("fields.nom", "fields.nom", null, null, null, "fields.info", "fields.ylat", "fields.xlong");
        ISFields isFields2 = new ISFields("fields.nom", "fields.nom", null, "fields.capacite", "fields.places_restantes", null, null, null);

        ISMapper isMapper1 = new ISMapper("JSONObject", "records", isFields1);
        ISMapper isMapper2 = new ISMapper("JSONObject", "records", isFields2);

        RequestData requestData1 = new RequestData("get", "www.someUrl.com", null, isMapper1);
        RequestData requestData2 = new RequestData("get", "www.someUrl.com", null, isMapper2);

        List<RequestData> requestsData = new ArrayList<>();
        requestsData.add(requestData1);
        requestsData.add(requestData2);

        ParkingManager parkingManager = new ParkingManager("Poitier", requestsData);
        mockMvc.perform(put("/parking-manager/city")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingManager)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistingParkingManagerException))
                .andExpect(result -> assertEquals(result.getResolvedException().getMessage(), "Parking manager already exists for city Poitier"));
    }

    @Test
    public void updateParkingManager_shouldSucceed() throws Exception {
        ISFields isFields1 = new ISFields("newFields.nom", "newFields.nom", null, null, null, "newFields.info", "newFields.ylat", "newFields.xlong");
        ISFields isFields2 = new ISFields("newFields.nom", "newFields.nom", null, "newFields.capacite", "newFields.places_restantes", null, null, null);

        ISMapper isMapper1 = new ISMapper("JSONObject", "records", isFields1);
        ISMapper isMapper2 = new ISMapper("JSONObject", "records", isFields2);

        RequestData requestData1 = new RequestData("get", "www.someNewUrl.com", null, isMapper1);
        RequestData requestData2 = new RequestData("get", "www.someNewUrl.com", null, isMapper2);

        List<RequestData> requestsData = new ArrayList<>();
        requestsData.add(requestData1);
        requestsData.add(requestData2);

        ParkingManager parkingManager = new ParkingManager("Poitier", requestsData);
        MvcResult mvcResult = mockMvc.perform(post("/parking-manager/city/Poitier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingManager)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        ParkingManager parkingManagerFromDB = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ParkingManager.class);
        assertNotNull(parkingManagerFromDB);
        parkingManager.setId(parkingManagerFromDB.getId());
        assertTrue(parkingManager.equals(parkingManagerFromDB));
    }

    @Test
    public void updateParkingManager_cityDoesntExists_shouldFail() throws Exception {
        ISFields isFields1 = new ISFields("newFields.nom", "newFields.nom", null, null, null, "newFields.info", "newFields.ylat", "newFields.xlong");
        ISFields isFields2 = new ISFields("newFields.nom", "newFields.nom", null, "newFields.capacite", "newFields.places_restantes", null, null, null);

        ISMapper isMapper1 = new ISMapper("JSONObject", "records", isFields1);
        ISMapper isMapper2 = new ISMapper("JSONObject", "records", isFields2);

        RequestData requestData1 = new RequestData("get", "www.someNewUrl.com", null, isMapper1);
        RequestData requestData2 = new RequestData("get", "www.someNewUrl.com", null, isMapper2);

        List<RequestData> requestsData = new ArrayList<>();
        requestsData.add(requestData1);
        requestsData.add(requestData2);

        ParkingManager parkingManager = new ParkingManager("Sophia-antipolis", requestsData);
        mockMvc.perform(post("/parking-manager/city/Sophia-antipolis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingManager)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NonExistingParkingManagerException))
                .andExpect(result -> assertEquals(result.getResolvedException().getMessage(), "No parking manager found for city Sophia-antipolis"));
    }

    @Test
    public void deleteParkingManager_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/parking-manager/city/Poitier")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        ParkingManager parkingManager = parkingManagerRepository.getByCity("Poitier");
        assertNull(parkingManager);
    }

    @Test
    public void deleteParkingManager_cityDoesntExists_shouldFail() throws Exception {
        mockMvc.perform(delete("/parking-manager/city/Sophia-antipolis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NonExistingParkingManagerException))
                .andExpect(result -> assertEquals(result.getResolvedException().getMessage(), "No parking manager found for city Sophia-antipolis"));
    }
}