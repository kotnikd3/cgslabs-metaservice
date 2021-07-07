package com.cgs.jt.rwis.metaservice.db.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "model")
@NamedQueries(
        {
                @NamedQuery(name = "com.cgs.jt.rwis.metaservice.db.entity.Model.getAll", query = "SELECT m FROM Model m")
        })
public class Model {
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(targetEntity = Subscription.class, mappedBy = "model")
    private List<Subscription> subscriptionList = new ArrayList<>();

    @OneToMany(targetEntity = MetroConfig.class, mappedBy = "model")
    private List<MetroConfig> metroConfigList = new ArrayList<>();

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

    public List<MetroConfig> getMetroConfigList() {
        return metroConfigList;
    }

    public void setMetroConfigList(List<MetroConfig> metroConfigList) {
        this.metroConfigList = metroConfigList;
    }

    @Override
    public String toString() {
        return "Model{" +
                ", name='" + name + '\'' +
                '}';
    }
}
