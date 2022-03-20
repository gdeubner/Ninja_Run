package com.ninjadroid.app.utils.containers;

import java.io.Serializable;

public class LocationContainer implements Serializable {
    double lon, lat, altitude;
    float speed;
    long time, elapsedRealtimeNs;

    public LocationContainer(double lat, double lon, double altitude, float speed, long time, long elapsedRealtimeNs) {
        this.lon = lon;
        this.lat = lat;
        this.altitude = altitude;
        this.speed = speed;
        this.time = time;
        this.elapsedRealtimeNs = elapsedRealtimeNs;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getElapsedRealtimeNs() {
        return elapsedRealtimeNs;
    }

    @Override
    public String toString() {
        return "LocationContainer{" +
                "lon=" + lon +
                ", lat=" + lat +
                ", altitude=" + altitude +
                ", speed=" + speed +
                ", time=" + time +
                '}';
    }
}
