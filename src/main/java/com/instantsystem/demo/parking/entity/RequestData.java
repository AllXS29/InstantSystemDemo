package com.instantsystem.demo.parking.entity;

import java.util.List;
import java.util.Objects;

/**
 * This class will contain the URL to reach to get data for a {@link Parking} entity, with the {@link org.springframework.http.HttpMethod}
 * as String and the parameters (not used for now) and the InstantSystem mapper used to convert the response to {@link Parking} entity
 */
public class RequestData {
    public String method;
    public String url;
    public List<String> parameters;
    public ISMapper iSMapper;

    public RequestData(String method, String url, List<String> parameters, ISMapper iSMapper) {
        this.method = method;
        this.url = url;
        this.parameters = parameters;
        this.iSMapper = iSMapper;
    }

    public RequestData() {
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public ISMapper getiSMapper() {
        return iSMapper;
    }

    public void setiSMapper(ISMapper iSMapper) {
        this.iSMapper = iSMapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestData that = (RequestData) o;
        return Objects.equals(method, that.method) && Objects.equals(url, that.url) && Objects.equals(parameters, that.parameters) && iSMapper.equals(that.iSMapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, url, parameters, iSMapper);
    }
}
