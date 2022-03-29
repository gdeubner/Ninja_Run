package com.ninjadroid.app.utils.containers.DirectionsContainers;

public class Step {
    Distance distance;
    Duration duration;
    StepEndLocation end_location;
    private String html_instructions;
    Polyline polyline;
    StepStartLocation start_location;
    private String travel_mode;

    // Getter Methods
    public Distance getDistance() {
        return distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public StepEndLocation getEnd_location() {
        return end_location;
    }

    public String getHtml_instructions() {
        return html_instructions;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public StepStartLocation getStart_location() {
        return start_location;
    }

    public String getTravel_mode() {
        return travel_mode;
    }
}
