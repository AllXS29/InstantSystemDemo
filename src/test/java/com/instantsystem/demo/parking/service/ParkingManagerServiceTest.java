package com.instantsystem.demo.parking.service;

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
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ParkingManagerServiceTest {

    @Autowired
    private ParkingManagerRepository parkingManagerRepository;

    @Autowired
    private ParkingManagerService parkingManagerService;

    private ParkingManager manager;

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
    public void getAll_shouldSucceed() {
        List<ParkingManager> parkingManagersFromDB = parkingManagerService.getAll();
        assertNotNull(parkingManagersFromDB);
        assertEquals(parkingManagersFromDB.size(), 1);
        assertEquals(manager, parkingManagersFromDB.get(0));
    }

    @Test
    public void getByCity_shouldSucceed() {
        ParkingManager parkingManagerFromDB = parkingManagerService.getByCity("Poitier");
        assertNotNull(parkingManagerFromDB);
        assertTrue(manager.equals(parkingManagerFromDB));
    }

    @Test
    public void getByCity_cityDoesntExists_shouldReturnNull() {
        NonExistingParkingManagerException thrown = assertThrows(NonExistingParkingManagerException.class, () -> {
            parkingManagerService.getByCity("Sophia-antipolis");
        }, "NonExistingParkingManagerException was expected");
    }

    @Test
    public void createParkingManager_shouldSucceed() {
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
        ParkingManager parkingManagerInserted = parkingManagerService.createParkingManager(parkingManager);
        assertNotNull(parkingManagerInserted);
        parkingManager.setId(parkingManagerInserted.getId());
        assertEquals(parkingManager, parkingManagerInserted);
    }

    @Test
    public void createParkingManager_cityAlreadySet_shouldFail() {
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
        AlreadyExistingParkingManagerException thrown = assertThrows(AlreadyExistingParkingManagerException.class, () -> {
            parkingManagerService.createParkingManager(parkingManager);

        }, "AlreadyExistingParkingManagerException was expected");
    }

    @Test
    public void updateParkingManager_shouldSucceed() {
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
        ParkingManager parkingManagerUpdated = parkingManagerService.updateParkingManager("Poitier", parkingManager);
        assertEquals(parkingManager, parkingManagerUpdated);
    }

    @Test
    public void updateParkingManager_cityDoesntExists_shouldFail() {
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
        NonExistingParkingManagerException thrown = assertThrows(NonExistingParkingManagerException.class, () -> {
            parkingManagerService.updateParkingManager("Sophia-antipolis", parkingManager);
        }, "NonExistingParkingManagerException was expected");
    }

    @Test
    public void deleteParkingManager_shouldSucceed() {
        parkingManagerService.deleteParkingManager("Poitier");
        ParkingManager parkingManager = parkingManagerRepository.getByCity("Poitier");
        assertNull(parkingManager);
    }

    @Test
    public void deleteParkingManager_cityDoesntExists_shouldFail() {
        NonExistingParkingManagerException thrown = assertThrows(NonExistingParkingManagerException.class, () -> {
            parkingManagerService.deleteParkingManager("Sophia-antipolis");
        }, "NonExistingParkingManagerException was expected");
    }
}