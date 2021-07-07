package com.cgs.jt.rwis.metaservice.db.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@IdClass(Location.LocationId.class)
@Table(name = "location")
public class Location {
    // Primary key class
    static public class LocationId implements Serializable {
        protected double latitude;
        protected double longitude;

        public LocationId() {}

        public LocationId(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocationId that = (LocationId) o;
            return Double.compare(that.latitude, latitude) == 0 &&
                    Double.compare(that.longitude, longitude) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(latitude, longitude);
        }
    }

    @Id
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Id
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @OneToMany(targetEntity = Subscription.class, mappedBy = "location")
    private List<Subscription> subscriptionList = new ArrayList<>();

    @OneToMany(targetEntity = MetroConfig.class, mappedBy = "location")
    private List<MetroConfig> metroConfigList = new ArrayList<>();

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
}

