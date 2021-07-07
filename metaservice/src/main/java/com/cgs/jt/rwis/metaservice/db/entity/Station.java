package com.cgs.jt.rwis.metaservice.db.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "station")
@NamedQueries(
        {
                @NamedQuery(name = "com.cgs.jt.rwis.metaservice.db.entity.Station.getAll", query = "SELECT s FROM Station s")
        })
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "elevation")
    private double elevation;

    @Column(name = "customer_id")
    protected String costumerId;

    @OneToMany(targetEntity = ParameterOnStation.class, mappedBy = "station")
    private Set<ParameterOnStation> parameterOnStationSet;

    @OneToMany(targetEntity = BaseCanSeeStation.class, mappedBy = "station")
    private Set<BaseCanSeeStation> baseCanSeeStationSet;

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

    public Set<ParameterOnStation> getParameterOnStationSet() {
        return parameterOnStationSet;
    }

    public void setParameterOnStationSet(Set<ParameterOnStation> parameterOnStationSet) {
        this.parameterOnStationSet = parameterOnStationSet;
    }

    public Set<BaseCanSeeStation> getBaseCanSeeStationSet() {
        return baseCanSeeStationSet;
    }

    public void setBaseCanSeeStationSet(Set<BaseCanSeeStation> baseCanSeeStationSet) {
        this.baseCanSeeStationSet = baseCanSeeStationSet;
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", elevation=" + elevation +
                '}';
    }
}
