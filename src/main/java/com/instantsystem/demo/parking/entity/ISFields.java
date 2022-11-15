package com.instantsystem.demo.parking.entity;

import java.util.Objects;

/**
 * This class will contain the fields of the {@link Parking} entity as variable name, and the path to use to retrieve
 * the data as value.
 */
public class ISFields {
    private String id;
    private String name;
    private String city;
    private String nbPlaces;
    private String nbPlacesRemaining;
    private String description;
    private String latitude;
    private String longitude;

    public ISFields(String id, String name, String city, String nbPlaces, String nbPlacesRemaining, String description, String latitude, String longitude) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.nbPlaces = nbPlaces;
        this.nbPlacesRemaining = nbPlacesRemaining;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ISFields() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNbPlaces() {
        return nbPlaces;
    }

    public void setNbPlaces(String nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public String getNbPlacesRemaining() {
        return nbPlacesRemaining;
    }

    public void setNbPlacesRemaining(String nbPlacesRemaining) {
        this.nbPlacesRemaining = nbPlacesRemaining;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ISFields isFields = (ISFields) o;
        return Objects.equals(id, isFields.id) && Objects.equals(name, isFields.name) && Objects.equals(city, isFields.city) && Objects.equals(nbPlaces, isFields.nbPlaces) && Objects.equals(nbPlacesRemaining, isFields.nbPlacesRemaining) && Objects.equals(description, isFields.description) && Objects.equals(latitude, isFields.latitude) && Objects.equals(longitude, isFields.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, city, nbPlaces, nbPlacesRemaining, description, latitude, longitude);
    }
}
