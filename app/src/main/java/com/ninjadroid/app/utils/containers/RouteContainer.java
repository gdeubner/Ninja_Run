package com.ninjadroid.app.utils.containers;

import java.io.Serializable;

public class RouteContainer implements Serializable {

    private userContainer user;
    private routeInfoContainer route;

    public int getUser_id() {
        return user.getUser_id();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public String getTown() {
        return route.getTown();
    }

    public int getDistance() {
        return route.getDistance();
    }

    public int getRoute_id() {
        return route.getRoute_id();
    }

    public double getLat_start() {
        return route.getLat_start();
    }

    public double getLong_start() {
        return route.getLong_start();
    }

    public double getLat_end() {
        return route.getLat_end();
    }

    public double getLong_end() {
        return route.getLong_end();
    }

    public String getRoute_f() {
        return route.getRoute_f();
    }
}
class userContainer implements Serializable {
    private int user_id;
    private String username;

    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }
}

class routeInfoContainer implements Serializable {
    private String town;
    private int distance;
    private int user_id;
    private int route_id;
    private double lat_start;
    private double long_start;
    private double lat_end;
    private double long_end;
    private String route_f;

    public String getTown() {
        return town;
    }

    public int getDistance() {
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
}