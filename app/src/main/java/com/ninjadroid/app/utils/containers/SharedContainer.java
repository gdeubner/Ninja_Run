package com.ninjadroid.app.utils.containers;

import java.io.Serializable;

public class SharedContainer implements Serializable {
    private int route_id;
    private String town;
    private float distance;
    private String username;
    private String title;

    // Getter Methods

    public int getRoute_id() {
        return route_id;
    }

    public String getTown() {
        return town;
    }

    public float getDistance() {
        return distance;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }


    // Setter Methods

    public void setRoute_id(int route_id) {
        this.route_id = route_id;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
