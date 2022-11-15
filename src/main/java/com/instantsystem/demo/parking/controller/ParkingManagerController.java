package com.instantsystem.demo.parking.controller;

import com.instantsystem.demo.parking.entity.ParkingManager;
import com.instantsystem.demo.parking.service.ParkingManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("parking-manager")
public class ParkingManagerController {

    private static final Logger LOG = LoggerFactory.getLogger(ParkingManagerController.class.getName());

    private ParkingManagerService parkingManagerService;

    public ParkingManagerController(ParkingManagerService parkingManagerService) {
        this.parkingManagerService = parkingManagerService;
    }

    /**
     * Retrieve all the instance of {@link ParkingManager} in DB
     * @return  A {@link List} of all the {@link ParkingManager} entities
     */
    @GetMapping()
    public List<ParkingManager> getAll() {
        LOG.info("Retrieving all parking managers in DB");
        return parkingManagerService.getAll();
    }

    /**
     * Retrieve the {@link ParkingManager} for the given city
     * @param city  The city of the parking manager
     * @return The {@link ParkingManager} entity
     */
    @GetMapping("/city/{city}")
    public ParkingManager getParkingManager(@PathVariable String city) {
        LOG.info("Retrieving parking manager for city {}", city);
        return parkingManagerService.getByCity(city);
    }

    /**
     * Create a new parking manager for a city
     * @param parkingManager    The Parking manager entity that will be saved in DB
     * @return  The saved {@link ParkingManager} entity
     */
    @PutMapping("/city")
    public ParkingManager createParkingManager(@RequestBody ParkingManager parkingManager) {
        LOG.info("Creating new parking manager for city {}, body {}", parkingManager.getCity(), parkingManager.toString());
        return parkingManagerService.createParkingManager(parkingManager);
    }

    /**
     * Update the existing {@link ParkingManager}
     * @param city              The city of the {@link ParkingManager}
     * @param parkingManager    The new object to replace the existing {@link ParkingManager}
     * @return  The updated {@link ParkingManager}
     */
    @PostMapping("/city/{city}")
    public ParkingManager updateParkingManager(@PathVariable String city, @RequestBody ParkingManager parkingManager) {
        LOG.info("Updating parking manager of city {}, body {}", city, parkingManager.toString());
        return parkingManagerService.updateParkingManager(city, parkingManager);
    }

    /**
     * Remove the {@link ParkingManager} associated with a city from the DB
     * @param city  The city associated with the {@link ParkingManager}
     */
    @DeleteMapping("/city/{city}")
    public void deleteParkingManager(@PathVariable String city) {
        LOG.info("Deleting parking manager for city {}", city);
        parkingManagerService.deleteParkingManager(city);
    }
}
