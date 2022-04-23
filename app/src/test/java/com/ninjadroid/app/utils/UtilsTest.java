package com.ninjadroid.app.utils;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void calcDistanceTraveled() {
    }

    @Test
    public void pound2kilogram() {
        assertEquals(2.26796, Utils.pound2kilogram(5));
    }

    @Test
    public void second2minute() {
        assertEquals(2, Utils.second2minute(120));
        assertEquals(0, Utils.second2minute(0));
        assertNotEquals(2, Utils.second2minute(120));
    }

    @Test
    public void getRunDuration() {
    }

    @Test
    public void formatDateTime() {
    }

    @Test
    public void simpleCalorieCalc() {
    }

    @Test
    public void findBearing() {
    }

    @Test
    public void findBearing2() {
    }

    @Test
    public void distanceBetweenLatLng() {
    }
}