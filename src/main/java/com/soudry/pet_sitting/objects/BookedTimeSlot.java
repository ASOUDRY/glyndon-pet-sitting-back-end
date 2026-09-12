package com.soudry.pet_sitting.objects;

import java.time.LocalTime;

public class BookedTimeSlot {

    private LocalTime startTime;
    private LocalTime endTime;
    private String serviceType;


    public BookedTimeSlot() {
    }


    public BookedTimeSlot(
            LocalTime startTime,
            LocalTime endTime
    ) {
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public BookedTimeSlot(
            LocalTime startTime,
            LocalTime endTime,
            String serviceType
    ) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.serviceType = serviceType;
    }


    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }


    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }


    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }


    @Override
    public String toString() {
        return "BookedTimeSlot{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", serviceType='" + serviceType + '\'' +
                '}';
    }
}