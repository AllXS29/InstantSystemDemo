package com.instantsystem.demo.parking.service;

import com.instantsystem.demo.parking.entity.ParkingManager;
import com.instantsystem.demo.parking.exception.AlreadyExistingParkingManagerException;
import com.instantsystem.demo.parking.exception.NonExistingParkingManagerException;
import com.instantsystem.demo.parking.repository.ParkingManagerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(ParkingManagerService.class.getName());

    public ParkingManagerRepository parkingManagerRepository;

    public ParkingManagerService(ParkingManagerRepository parkingManagerRepository) {
        this.parkingManagerRepository = parkingManagerRepository;
    }

    /**
     * Retrieve all the {@link ParkingManager} entities from the DB
     * @return a {@link List} of all the {@link ParkingManager}
     */
    public List<ParkingManager> getAll() {
        LOG.info("Retrieving all parking managers in DB");
        return parkingManagerRepository.findAll();
    }

    /**
     * Retrieve a {@link ParkingManager} from a city
     * @param city  The city associated with the {@link ParkingManager}
     * @return The {@link ParkingManager}
     * @throws NonExistingParkingManagerException if no {@link ParkingManager} is found for the given city
     */
    public ParkingManager getByCity(String city) {
        LOG.info("Retrieve ParkingManager for city {}", city);
        ParkingManager parkingManager = parkingManagerRepository.getByCity(city);
        if (parkingManager == null) {
            throw new NonExistingParkingManagerException(city);
        }
        return parkingManager;
    }

    /**
     * Create a {@link ParkingManager}
     * @param parkingManager The {@link ParkingManager} object that will be inserted in DB
     * @return The created {@link ParkingManager}
     * @throws AlreadyExistingParkingManagerException if a {@link ParkingManager} is already in DB for the same city
     */
    public ParkingManager createParkingManager(ParkingManager parkingManager) {
        LOG.info("Creating new parking manager for city {}, body {}", parkingManager.getCity(), parkingManager.toString());
        ParkingManager existingPM = parkingManagerRepository.getByCity(parkingManager.getCity());
        if (existingPM != null) {
            LOG.warn("Tried to created a parking manager for city {}, already exists id: {}", parkingManager.getCity(), existingPM.getId());
            throw new AlreadyExistingParkingManagerException(parkingManager.getCity());
        }
        return parkingManagerRepository.insert(parkingManager);
    }

    /**
     * Update an existing {@link ParkingManager}
     * @param city              The city associated with the {@link ParkingManager} to update
     * @param parkingManager    The object that will be inserted as the new DB object
     * @return  The update {@link ParkingManager}
     * @throws NonExistingParkingManagerException if the city has no associated {@link ParkingManager}
     */
    public ParkingManager updateParkingManager(String city, ParkingManager parkingManager) {
        LOG.info("Updating parking manager of city {}, body {}", city, parkingManager.toString());
        ParkingManager existingPM = parkingManagerRepository.getByCity(city);
        if (existingPM == null) {
            LOG.warn("Tried to update a parking manager for city {}, doesn't exists", city);
            throw new NonExistingParkingManagerException(city);
        }
        parkingManager.setId(existingPM.getId());
        // Just in case someone tries to update a city PM with another city
        parkingManager.setCity(city);
        return parkingManagerRepository.save(parkingManager);
    }

    /**
     * Remove a {@link ParkingManager} from the DB
     * @param city  The city associated with the {@link ParkingManager} to delete
     * @throws NonExistingParkingManagerException if the city has no associated {@link ParkingManager}
     */
    public void deleteParkingManager(String city) {
        LOG.info("Deleting parking manager for city {}", city);
        ParkingManager toDelete = parkingManagerRepository.getByCity(city);
        if (toDelete == null) {
            LOG.warn("Tried to delete a parking manager for city {}, doesn't exists", city);
            throw new NonExistingParkingManagerException(city);
        }
        parkingManagerRepository.delete(toDelete);
    }
}
