package com.instantsystem.demo.parking.entity;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {

    private Double latitude;

    private Double longitude;

    public Position(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Position() {
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.latitude, latitude) == 0 && Double.compare(position.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
