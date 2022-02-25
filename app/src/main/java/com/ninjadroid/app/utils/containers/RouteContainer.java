package com.ninjadroid.app.utils.containers;

import java.io.Serializable;

public class RouteContainer implements Serializable {
    double var_lat_start;
    double var_long_start;
    double var_lat_end;
    double var_long_end;
    String var_town;
    double var_dist;
    int var_uid;
    String var_routf;

    public RouteContainer(){ }

    public RouteContainer(double var_lat_start, double var_long_start, double var_lat_end,
                          double var_long_end, String var_town, double var_dist, int var_uid,
                          String var_routf){
        this.var_lat_start = var_lat_start;
        this.var_long_start =var_long_start;
        this.var_lat_end =var_lat_end;
        this.var_long_end =var_long_end;
        this.var_town =var_town;
        this.var_dist =var_dist;
        this.var_uid =var_uid;
        this.var_routf =var_routf;
    }

    public double getVar_lat_start() {
        return var_lat_start;
    }

    public void setVar_lat_start(double var_lat_start) {
        this.var_lat_start = var_lat_start;
    }

    public double getVar_long_start() {
        return var_long_start;
    }

    public void setVar_long_start(double var_long_start) {
        this.var_long_start = var_long_start;
    }

    public double getVar_lat_end() {
        return var_lat_end;
    }

    public void setVar_lat_end(double var_lat_end) {
        this.var_lat_end = var_lat_end;
    }

    public double getVar_long_end() {
        return var_long_end;
    }

    public void setVar_long_end(double var_long_end) {
        this.var_long_end = var_long_end;
    }

    public String getVar_town() {
        return var_town;
    }

    public void setVar_town(String var_town) {
        this.var_town = var_town;
    }

    public double getVar_dist() {
        return var_dist;
    }

    public void setVar_dist(double var_dist) {
        this.var_dist = var_dist;
    }

    public int getVar_uid() {
        return var_uid;
    }

    public void setVar_uid(int var_uid) {
        this.var_uid = var_uid;
    }

    public String getVar_routf() {
        return var_routf;
    }

    public void setVar_routf(String var_routf) {
        this.var_routf = var_routf;
    }
}
