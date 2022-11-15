package com.instantsystem.demo.parking.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Document("ParkingManager")
public class ParkingManager implements Serializable {
    @Id
    private String id;

    @Indexed(unique = true)
    private String city;

    private List<RequestData> requestsData;

    public ParkingManager(String city, List<RequestData> requestsData) {
        this.city = city;
        this.requestsData = requestsData;
    }

    public ParkingManager() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<RequestData> getRequestsData() {
        return requestsData;
    }

    public void setRequestsData(List<RequestData> requestsData) {
        this.requestsData = requestsData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingManager that = (ParkingManager) o;
        return Objects.equals(id, that.id) && Objects.equals(city, that.city) && requestsData.equals(that.requestsData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, requestsData);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", requestsData=" + requestsData +
                '}';
    }
}
