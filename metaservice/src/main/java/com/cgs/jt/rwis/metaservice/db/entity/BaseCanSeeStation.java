package com.cgs.jt.rwis.metaservice.db.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(BaseCanSeeStation.BaseCanSeeStationId.class)
@Table(name = "base_can_see_station")
@NamedQueries(
        {
                @NamedQuery(name = "com.cgs.jt.rwis.metaservice.db.entity.BaseCanSeeStation.getAll", query = "SELECT b FROM BaseCanSeeStation b")
        })
public class BaseCanSeeStation {
    // Class for primary key.
    static public class BaseCanSeeStationId implements Serializable {
        protected int base;
        protected Station station;

        public BaseCanSeeStationId() {
        }

        public BaseCanSeeStationId(int base, Station station) {
            this.base = base;
            this.station = station;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BaseCanSeeStationId that = (BaseCanSeeStationId) o;
            return base == that.base &&
                    Objects.equals(station, that.station);
        }

        @Override
        public int hashCode() {
            return Objects.hash(base, station);
        }
    }

    @Id
    @Column(name = "base_id")
    private int base;

    @Id
    @ManyToOne()
    @JoinColumn(name = "station_id", foreignKey=@ForeignKey(name = "station_fk"))
    protected Station station;

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return "BaseCanSeeStation{" +
                "base=" + base +
                ", station=" + station +
                '}';
    }
}
