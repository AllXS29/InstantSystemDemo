package com.instantsystem.demo.parking.entity;

import java.io.Serializable;
import java.util.Objects;

public class Parking implements Serializable {
    private String id;
    private String name;
    private String city;
    private Integer nbPlaces;
    private Integer nbPlacesRemaining;
    private String description;
    private Position position;

    public Parking(String id, String name, String city, Integer nbPlaces, Integer nbPlacesRemaining, String description, Position position) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.nbPlaces = nbPlaces;
        this.nbPlacesRemaining = nbPlacesRemaining;
        this.description = description;
        this.position = position;
    }

    public Parking() {
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

    public Integer getNbPlaces() {
        return nbPlaces;
    }

    public void setNbPlaces(Integer nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public Integer getNbPlacesRemaining() {
        return nbPlacesRemaining;
    }

    public void setNbPlacesRemaining(Integer nbPlacesRemaining) {
        this.nbPlacesRemaining = nbPlacesRemaining;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parking parking = (Parking) o;
        return Objects.equals(id, parking.id) && Objects.equals(name, parking.name) && Objects.equals(city, parking.city) && Objects.equals(nbPlaces, parking.nbPlaces) && Objects.equals(nbPlacesRemaining, parking.nbPlacesRemaining) && Objects.equals(description, parking.description) && Objects.equals(position, parking.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, city, nbPlaces, nbPlacesRemaining, description, position);
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", nbPlaces=" + nbPlaces +
                ", nbPlacesRemaining=" + nbPlacesRemaining +
                ", description='" + description + '\'' +
                ", position=" + position +
                '}';
    }
}
