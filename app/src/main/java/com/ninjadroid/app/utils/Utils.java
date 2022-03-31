package com.ninjadroid.app.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.ninjadroid.app.utils.containers.LocationContainer;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public  class Utils {
    //used for calculating the distance between two lat/lon coordinates
    public static double calcDistanceTraveled(ArrayList<LocationContainer> list){
        double dist = 0;
        for (int i = 0; i < list.size()-1; i++) {
            dist += distance(list.get(i).getLat(), list.get(i).getLon(),
                    list.get(i+1).getLat(), list.get(i+1).getLon(), 'M');
        }
        //rounds dist to first decimal place
        double tempDist = Math.round(dist * 10);
        dist = tempDist / 10;
        return dist;
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
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

    private static double nano2seconds(double nano){
        return nano / Math.pow(10,9);
    }

    public static double pound2kilogram(double lb) {
        return lb * 0.453592;
    }

    public static double second2minute(double sec) {
        return sec / 60;
    }


    public static int getRunDuration(ArrayList<LocationContainer> list){
        return (int) nano2seconds((list.get(list.size()-1).getElapsedRealtimeNs()
                - list.get(0).getElapsedRealtimeNs()));
    }

    //time is in miliseconds since Unix epoch time
    public static String formatDateTime(long time){
        //DateTimeFormatter formatterIn = DateTimeFormatter.ofPattern("HH:mm:ss M dd, uuuu Z");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(time);
    }

    /**
     * @param time  - the duration of the run in seconds
     * @param weight - weight of the runner in lb
     * @return Estimates the calories burned during the course of a run.
     *     Assumes an average constant speed of 8 km/h (which = 13.5 MET).
     */
    public static int simpleCalorieCalc(double time, double weight) {
        //convert lb to kg
        weight = Utils.pound2kilogram(weight);
        //convert time to minutes
        time = Utils.second2minute(time);
        double MET = 13.5;
        int calories =  (int)(time * (MET * 3.5 * weight) / 200);
        return calories;
    }

    public static float findBearing(LatLng curPos, LatLng routePnt){
        //vector 1 (U)
        double u1 = routePnt.latitude - curPos.latitude;
        double u2 = routePnt.longitude - curPos.longitude;

        //vector 2 (V)
        double v1 = 0;
        double v2 = 1;

        double dotProd = u1*v1 + u2*v2;

        double vMag = Math.sqrt(Math.pow(v1, 2) + Math.pow(v2, 2));
        double uMag = Math.sqrt(Math.pow(u1, 2) + Math.pow(u2, 2));

        double angleRad = Math.acos(dotProd/(vMag * uMag));

        double angleDeg = Math.toDegrees(angleRad);
        return (float) angleDeg + 90;
    }

    public static float findBearing2(LatLng curPos, LatLng routePnt){
        double lat1 = curPos.latitude;
        double long1 = curPos.longitude;
        double lat2 = routePnt.latitude;
        double long2 = routePnt.longitude;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise

        return (float)brng;
    }

    public static float distanceBetweenLatLng(LatLng center, LatLng target){
        float[] results = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                target.latitude, target.longitude, results);
        return  results[0];
    }
}
