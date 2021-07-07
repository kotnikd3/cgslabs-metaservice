package com.cgs.jt.rwis.metaservice.api;

import java.util.Set;

public class StationDTO {
    private Integer id;

    private String name;

    private double latitude;

    private double longitude;

    private double elevation;

    protected String costumerId;

    private Set<ParameterOnStationDTO> parameterOnStationSet;

    private Set<BaseCanSeeStationDTO> baseCanSeeStationSet;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public String getCostumerId() {
        return costumerId;
    }

    public void setCostumerId(String costumerId) {
        this.costumerId = costumerId;
    }

    public Set<ParameterOnStationDTO> getParameterOnStationSet() {
        return parameterOnStationSet;
    }

    public void setParameterOnStationSet(Set<ParameterOnStationDTO> parameterOnStationSet) {
        this.parameterOnStationSet = parameterOnStationSet;
    }

    public Set<BaseCanSeeStationDTO> getBaseCanSeeStationSet() {
        return baseCanSeeStationSet;
    }

    public void setBaseCanSeeStationSet(Set<BaseCanSeeStationDTO> baseCanSeeStationSet) {
        this.baseCanSeeStationSet = baseCanSeeStationSet;
    }
}
