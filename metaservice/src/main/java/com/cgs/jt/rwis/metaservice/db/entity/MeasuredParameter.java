package com.cgs.jt.rwis.metaservice.db.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "measured_parameter")
@NamedQueries(
        {
                @NamedQuery(name = "com.cgs.jt.rwis.metaservice.db.entity.MeasuredParameter.getAll", query = "SELECT p FROM MeasuredParameter p"),
        })
public class MeasuredParameter {
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(targetEntity = ParameterOnStation.class, mappedBy = "measuredParameter")
    private Set<ParameterOnStation> parameterOnStationSet = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ParameterOnStation> getParameterOnStationSet() {
        return parameterOnStationSet;
    }

    public void setParameterOnStationSet(Set<ParameterOnStation> parameterOnStationSet) {
        this.parameterOnStationSet = parameterOnStationSet;
    }

    @Override
    public String toString() {
        return "MeasuredParameter{" +
                "name='" + name + '\'' +
                ", parameterOnStationSet=" + parameterOnStationSet +
                '}';
    }
}

