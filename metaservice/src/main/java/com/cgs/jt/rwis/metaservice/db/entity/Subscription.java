package com.cgs.jt.rwis.metaservice.db.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(Subscription.SubscriptionId.class)
@Table(name = "subscription", indexes = {
        @Index(columnList = "model_name", name = "model_idx"),
        @Index(columnList = "model_name,latitude,longitude", name = "model_location_idx")})
@NamedQueries(
        {
                @NamedQuery(name = "com.cgs.jt.rwis.metaservice.db.entity.Subscription.getAll", query = "SELECT s FROM Subscription s"),
                @NamedQuery(name = "com.cgs.jt.rwis.metaservice.db.entity.Subscription.getAllByModelName", query = "SELECT s FROM Subscription s WHERE s.model.name = :model_name"),
                @NamedQuery(name = "com.cgs.jt.rwis.metaservice.db.entity.Subscription.getAllByModelNameAndLocation", query = "SELECT s FROM Subscription s " +
                        "WHERE s.model.name = :model_name AND s.location.latitude = :latitude AND s.location.longitude = :longitude")
        })
public class Subscription {
    // Primary key class
    static public class SubscriptionId implements Serializable {
        protected Location location;
        protected Model model;
        protected ForecastedParameter forecastedParameter;
        protected String customerId;

        public SubscriptionId() { }

        public SubscriptionId(Location location, Model model, ForecastedParameter forecastedParameter, String customerId) {
            this.location = location;
            this.model = model;
            this.forecastedParameter = forecastedParameter;
            this.customerId = customerId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SubscriptionId that = (SubscriptionId) o;
            return Objects.equals(location, that.location) &&
                    Objects.equals(model, that.model) &&
                    Objects.equals(forecastedParameter, that.forecastedParameter) &&
                    Objects.equals(customerId, that.customerId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, model, forecastedParameter, customerId);
        }
    }

    @Id
    @ManyToOne()
    @JoinColumn(name = "model_name", referencedColumnName="name", foreignKey=@ForeignKey(name = "model_fk"))
    private Model model;

    @Id
    @Column(name = "customer_id")
    private String customerId;

    @Id
    @ManyToOne()
    @JoinColumn(name="forecasted_parameter_name", referencedColumnName="name", insertable=false, updatable=false, foreignKey=@ForeignKey(name = "forecasted_parameter_fk"))
    private ForecastedParameter forecastedParameter;

    @Id
    @ManyToOne()
    @JoinColumns(value = {
            @JoinColumn(name="latitude", referencedColumnName="latitude", insertable=false, updatable=false),
            @JoinColumn(name="longitude", referencedColumnName="longitude", insertable=false, updatable=false)
    }, foreignKey=@ForeignKey(name = "location_fk"))
    private Location location;

    @Column(name = "elevation")
    private double elevation;


    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public ForecastedParameter getForecastedParameter() {
        return forecastedParameter;
    }

    public void setForecastedParameter(ForecastedParameter forecastedParameter) {
        this.forecastedParameter = forecastedParameter;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return Double.compare(that.elevation, elevation) == 0 &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elevation, location);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                ", model=" + model +
                ", customerId=" + customerId +
                ", forecastedParameter=" + forecastedParameter +
                ", location=" + location +
                ", elevation=" + elevation +
                '}';
    }
}
