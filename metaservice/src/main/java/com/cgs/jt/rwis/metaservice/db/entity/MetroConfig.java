package com.cgs.jt.rwis.metaservice.db.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(MetroConfig.MetroConfigId.class)
@Table(name = "metro_config")
@NamedQueries(
        {
                @NamedQuery(name = "com.cgs.jt.rwis.metaservice.db.entity.MetroConfig.getAll", query = "SELECT m FROM MetroConfig m")
        })
public class MetroConfig {
    // Primary key class
    static public class MetroConfigId implements Serializable {
        protected Location location;
        protected Model model;

        public MetroConfigId() {
        }

        public MetroConfigId(Location location, Model model) {
            this.location = location;
            this.model = model;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MetroConfigId that = (MetroConfigId) o;
            return Objects.equals(location, that.location) &&
                    Objects.equals(model, that.model);
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, model);
        }
    }


    @Id
    @ManyToOne()
    @JoinColumn(name = "model_name", referencedColumnName="name", foreignKey=@ForeignKey(name = "model_fk"))
    private Model model;

    @Id
    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name="latitude", referencedColumnName="latitude", insertable=false, updatable=false),
            @JoinColumn(name="longitude", referencedColumnName="longitude", insertable=false, updatable=false)
    }, foreignKey=@ForeignKey(name = "location_fk"))
    private Location location;

    @Column(name = "config")
    private String config;


    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "MetroConfig{" +
                ", model=" + model +
                ", config='" + config + '\'' +
                ", location=" + location +
                '}';
    }
}
