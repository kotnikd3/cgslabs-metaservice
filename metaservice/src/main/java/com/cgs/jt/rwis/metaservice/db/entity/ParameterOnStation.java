package com.cgs.jt.rwis.metaservice.db.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(ParameterOnStation.ParameterOnStationId.class)
@Table(name = "parameter_on_station")
public class ParameterOnStation {
    // Primary key class
    static public class ParameterOnStationId implements Serializable {
        protected MeasuredParameter measuredParameter;
        protected Station station;
        protected String sensorNum;

        public ParameterOnStationId() {}

        public ParameterOnStationId(Station station, MeasuredParameter measuredParameter, String sensorNum) {
            this.station = station;
            this.measuredParameter = measuredParameter;
            this.sensorNum = sensorNum;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParameterOnStationId that = (ParameterOnStationId) o;
            return Objects.equals(measuredParameter, that.measuredParameter) &&
                    Objects.equals(station, that.station) &&
                    Objects.equals(sensorNum, that.sensorNum);
        }

        @Override
        public int hashCode() {
            return Objects.hash(measuredParameter, station, sensorNum);
        }
    }

    @Id
    @ManyToOne()
    @JoinColumn(name = "station_id", foreignKey=@ForeignKey(name = "station_fk"))
    private Station station;

    @Id
    @Column(name = "sensor_num")
    private String sensorNum;

    @Id
    @ManyToOne()
    @JoinColumn(name="measured_parameter_name", referencedColumnName="name", insertable=false, updatable=false, foreignKey=@ForeignKey(name = "measured_parameter_fk"))
    private MeasuredParameter measuredParameter;

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public String getSensorNum() {
        return sensorNum;
    }

    public void setSensorNum(String sensorNum) {
        this.sensorNum = sensorNum;
    }

    public MeasuredParameter getMeasuredParameter() {
        return measuredParameter;
    }

    public void setMeasuredParameter(MeasuredParameter measuredParameter) {
        this.measuredParameter = measuredParameter;
    }

    @Override
    public String toString() {
        return "ParameterOnStation{" +
                "station=" + station +
                ", sensorNum='" + sensorNum + '\'' +
                ", measuredParameter=" + measuredParameter +
                '}';
    }
}
