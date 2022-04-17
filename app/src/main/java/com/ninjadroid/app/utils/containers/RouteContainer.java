package com.ninjadroid.app.utils.containers;

import java.io.Serializable;

public class RouteContainer implements Serializable {
    private String town;
    private double distance;
    private int user_id;
    private int route_id;
    private double lat_start;
    private double long_start;
    private double lat_end;
    private double long_end;
    private String route_f;
    private String username;
    private String title;
    private String date;

    public String getTown() {
        return town;
    }

    public double getDistance() {
        return distance;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getRoute_id() {
        return route_id;
    }

    public double getLat_start() {
        return lat_start;
    }

    public double getLong_start() {
        return long_start;
    }

    public double getLat_end() {
        return lat_end;
    }

    public double getLong_end() {
        return long_end;
    }

    public String getRoute_f() {
        return route_f;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }


}
