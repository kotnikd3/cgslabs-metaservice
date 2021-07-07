package com.cgs.jt.rwis.metaservice.api;

public class ParameterOnStationDTO {

    private String sensorNum;

    private MeasuredParameterDTO measuredParameter;

    public String getSensorNum() {
        return sensorNum;
    }

    public void setSensorNum(String sensorNum) {
        this.sensorNum = sensorNum;
    }

    public MeasuredParameterDTO getMeasuredParameter() {
        return measuredParameter;
    }

    public void setMeasuredParameter(MeasuredParameterDTO measuredParameter) {
        this.measuredParameter = measuredParameter;
    }
}
