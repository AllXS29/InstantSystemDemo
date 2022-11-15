package com.instantsystem.demo.parking.entity;

import java.util.Objects;

/**
 * This class will contain the Response type we expect from the URL call ({@link org.json.JSONObject} only handle for now)
 * The path to the List field in the response and the InstantSystem fields for the {@link Parking} entity
 */
public class ISMapper {
    private String responseType;
    private String listFieldPath;
    private ISFields iSFields;

    public ISMapper(String responseType, String listFieldPath, ISFields iSFields) {
        this.responseType = responseType;
        this.listFieldPath = listFieldPath;
        this.iSFields = iSFields;
    }

    public ISMapper() {
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getListFieldPath() {
        return listFieldPath;
    }

    public void setListFieldPath(String listFieldPath) {
        this.listFieldPath = listFieldPath;
    }

    public ISFields getiSFields() {
        return iSFields;
    }

    public void setiSFields(ISFields iSFields) {
        this.iSFields = iSFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ISMapper isMapper = (ISMapper) o;
        return Objects.equals(responseType, isMapper.responseType) && Objects.equals(listFieldPath, isMapper.listFieldPath) && iSFields.equals(isMapper.iSFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseType, listFieldPath, iSFields);
    }
}
