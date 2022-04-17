package com.ninjadroid.app.utils.containers;

import java.io.Serializable;

public class UserRouteContainer implements Serializable {

    private UserContainer user;
    private RouteContainer route;

    public int getUser_id() {
        return user.getUser_id();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public String getTown() {
        return route.getTown();
    }

    public double getDistance() {
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

    public RouteContainer getRoute(){return route;}
}
class UserContainer implements Serializable {
    private int user_id;
    private String username;

    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }
}