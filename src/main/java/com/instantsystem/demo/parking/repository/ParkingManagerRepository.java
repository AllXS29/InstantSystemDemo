package com.instantsystem.demo.parking.repository;

import com.instantsystem.demo.parking.entity.ParkingManager;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("ParkingManager")
public interface ParkingManagerRepository extends MongoRepository<ParkingManager, String> {

    ParkingManager getByCity(String city);
}
