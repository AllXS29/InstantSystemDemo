package com.instantsystem.demo.parking.controller;

import com.instantsystem.demo.parking.entity.Parking;
import com.instantsystem.demo.parking.service.ParkingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("parkings")
public class ParkingController {

    private static final Logger LOG = LoggerFactory.getLogger(ParkingController.class.getName());

    public ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    /**
     * Retrieve all the parking of a given city. If the position(latitude, longitude) is given we return only the
     * parkings within range of the position (default range 500m)
     * @param city  The city we want to retrieve the parking from
     * @param lat   The user's latitude
     * @param lon   The user's longitude
     * @param range The range, in meter, around the user to look for parkings (default 500m)
     * @return The list of parkings in the city or around the user's position
     */
    @GetMapping("/city/{city}")
    @ResponseBody
    public List<Parking> getCityParkings(@PathVariable String city, @RequestParam(required = false) Double lat, @RequestParam(required = false) Double lon, @RequestParam(required = false, defaultValue = "0.5") Double range) {
        LOG.info("Retrieve parkings for city {}, in range {}, user's position lat : {}, lon : {}", city, range, lat, lon);
        return parkingService.getParkings(city, lat, lon, range);
    }

    /**
     * Retrieve a specific parking by name
     * @param city The city of the parking
     * @param name The name of the parking
     * @return The parking entity
     */
    @GetMapping("/city/{city}/name/{name}")
    @ResponseBody
    public Parking getParking(@PathVariable String city, @PathVariable String name) {
        LOG.info("Retrieve parking in city {}, with name {}", city, name);
        return parkingService.getParking(city, name);
    }
}
