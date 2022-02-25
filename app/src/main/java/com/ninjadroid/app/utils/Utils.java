package com.ninjadroid.app.utils;

import android.location.Location;

import java.util.ArrayList;

public  class Utils {
    //used for calculating the distance between two lat/lon coordinates
    public static double calcDistanceTraveled(ArrayList<Location> list){
        double dist = 0;
        for (int i = 0; i < list.size()-1; i++) {
            dist += distance(list.get(i).getLatitude(), list.get(i).getLongitude(),
                    list.get(i+1).getLatitude(), list.get(i+1).getLongitude(), 'M');
        }
        return dist;
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
