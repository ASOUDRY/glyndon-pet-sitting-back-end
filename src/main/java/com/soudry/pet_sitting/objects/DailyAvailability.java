package com.soudry.pet_sitting.objects;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyAvailability {

    private LocalDate date;
    private boolean houseSittingAvailable;
    private boolean dropInsAvailable;
    private List<BookedTimeSlot> bookedTimes;

    public DailyAvailability() {
        this.houseSittingAvailable = true;
        this.dropInsAvailable = true;
        this.bookedTimes = new ArrayList<>();
    }

    public DailyAvailability(LocalDate date) {
        this.date = date;
        this.houseSittingAvailable = true;
        this.dropInsAvailable = true;
        this.bookedTimes = new ArrayList<>();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isHouseSittingAvailable() {
        return houseSittingAvailable;
    }

    public void setHouseSittingAvailable(boolean houseSittingAvailable) {
        this.houseSittingAvailable = houseSittingAvailable;
    }

    public boolean isDropInsAvailable() {
        return dropInsAvailable;
    }

    public void setDropInsAvailable(boolean dropInsAvailable) {
        this.dropInsAvailable = dropInsAvailable;
    }

    public List<BookedTimeSlot> getBookedTimes() {
        return bookedTimes;
    }

    public void setBookedTimes(List<BookedTimeSlot> bookedTimes) {
        this.bookedTimes = bookedTimes;
    }

    public void addBookedTime(BookedTimeSlot bookedTimeSlot) {
        this.bookedTimes.add(bookedTimeSlot);
    }
}