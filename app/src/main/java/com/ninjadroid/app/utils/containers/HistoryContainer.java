package com.ninjadroid.app.utils.containers;

import java.io.Serializable;

public class HistoryContainer implements Serializable {
    private int route_id;
    private String datetime;
    private int calories;
    private int duration;
    private float distance;
    private String town;
    private String title;



// Getter Methods

    public int getRoute_id() {
        return route_id;
    }

    public String getDatetime() {
        return datetime;
    }

    public int getCalories() {
        return calories;
    }

    public int getDuration() {
        return duration;
    }

    public float getDistance() {
        return distance;
    }

    public String getTown() {
        return town;
    }

    public String getTitle() {
        return title;
    }

    // Setter Methods

    public void setRoute_id(int route_id) {
        this.route_id = route_id;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
