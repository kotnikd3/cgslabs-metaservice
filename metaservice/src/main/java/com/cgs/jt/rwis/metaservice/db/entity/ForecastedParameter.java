package com.cgs.jt.rwis.metaservice.db.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "forecasted_parameter")
@NamedQueries(
        {
                @NamedQuery(name = "com.cgs.jt.rwis.metaservice.db.entity.ForecastedParameter.getAll", query = "SELECT p FROM ForecastedParameter p"),
        })
public class ForecastedParameter {
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(targetEntity = Subscription.class, mappedBy = "forecastedParameter")
    private List<Subscription> subscriptionList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Subscription> getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(List<Subscription> subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    @Override
    public String toString() {
        return "ForecastedParameter{" +
                "name='" + name + '\'' +
                '}';
    }
}
