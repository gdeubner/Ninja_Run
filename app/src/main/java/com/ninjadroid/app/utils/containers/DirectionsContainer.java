package com.ninjadroid.app.utils.containers;

import java.util.ArrayList;

public class DirectionsContainer {
    ArrayList<GeocodedWaypoints> geocoded_waypoints = new ArrayList<GeocodedWaypoints>();
    ArrayList <Route> routes = new ArrayList<Route>();
    private String status;

    // Getter Methods
    public String getStatus() {
        return status;
    }

    public ArrayList<GeocodedWaypoints> getGeocoded_waypoints() {
        return geocoded_waypoints;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public String getPolyline(){
        return routes.get(0).getLegs().get(0).getSteps().get(0).getPolyline().getPoints();
    }
}

class GeocodedWaypoints {
    private String geocoder_status;
    private String place_id;
    ArrayList<String> types = new ArrayList<String>();

    // Getter Methods
    public String getGeocoder_status() {
        return geocoder_status;
    }
    public String getPlace_id() {
        return place_id;
    }
    public ArrayList<String> getTypes(){
        return types;
    }

}

class Route{
    Bounds bounds;
    private String copyrights;
    ArrayList <Leg> legs = new ArrayList<Leg>();
    OverviewPolyline Overview_polylineObject;
    private String summary;
    ArrayList < Object > warnings = new ArrayList < Object > ();
    ArrayList < Object > waypoint_order = new ArrayList < Object > ();
    // Getter Methods
    public Bounds getBounds() {
        return bounds;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public OverviewPolyline getOverview_polyline() {
        return Overview_polylineObject;
    }

    public String getSummary() {
        return summary;
    }

    public ArrayList<Leg> getLegs(){
        return legs;
    }
}
class OverviewPolyline {
    private String points;
    // Getter Methods

    public String getPoints() {
        return points;
    }
}
class Bounds {
    Northeast northeast;
    Southwest southwest;
    // Getter Methods
    public Northeast getNortheast() {
        return northeast;
    }

    public Southwest getSouthwest() {
        return southwest;
    }
}
class Southwest {
    private float lat;
    private float lng;
    // Getter Methods
    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }
}
class Northeast {
    private float lat;
    private float lng;

    // Getter Methods
    public float getLat() {
        return lat;
    }
    public float getLng() {
        return lng;
    }
}
class Leg{
    Distance distance;
    Duration duration;
    private String end_address;
    EndLocation endLocation;
    private String start_address;
    StartLocation startLocation;
    ArrayList <Step> steps = new ArrayList<Step>();
    ArrayList < Object > traffic_speed_entry = new ArrayList < Object > ();
    ArrayList < Object > via_waypoint = new ArrayList < Object > ();

    // Getter Methods
    public Distance getDistance() {
        return distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getEnd_address() {
        return end_address;
    }

    public EndLocation getEnd_location() {
        return endLocation;
    }

    public String getStart_address() {
        return start_address;
    }

    public StartLocation getStart_location() {
        return startLocation;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }
}
class StartLocation {
    private float lat;
    private float lng;

    // Getter Methods
    public float getLat() {
        return lat;
    }
    public float getLng() {
        return lng;
    }
}
class EndLocation {
    private float lat;
    private float lng;

    // Getter Methods
    public float getLat() {
        return lat;
    }
    public float getLng() {
        return lng;
    }
}
class Duration {
    private String text;
    private float value;
    // Getter Methods

    public String getText() {
        return text;
    }

    public float getValue() {
        return value;
    }
}
class Distance {
    private String text;
    private float value;
    //Getter Methods
    public String getText() {
        return text;
    }
    public float getValue() {
        return value;
    }
}

class Step {
    Distance DistanceObject;
    Duration DurationObject;
    StepEndLocation endLocationObject;
    private String html_instructions;
    Polyline PolylineObject;
    StepStartLocation startLocationObject;
    private String travel_mode;

    // Getter Methods
    public Distance getDistance() {
        return DistanceObject;
    }

    public Duration getDuration() {
        return DurationObject;
    }

    public StepEndLocation getEnd_location() {
        return endLocationObject;
    }

    public String getHtml_instructions() {
        return html_instructions;
    }

    public Polyline getPolyline() {
        return PolylineObject;
    }

    public StepStartLocation getStart_location() {
        return startLocationObject;
    }

    public String getTravel_mode() {
        return travel_mode;
    }
}
class StepStartLocation {
    private float lat;
    private float lng;

    // Getter Methods
    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }
}
class Polyline {
    private String points;

    // Getter Methods
    public String getPoints() {
        return points;
    }
}
class StepEndLocation {
    private float lat;
    private float lng;

    // Getter Methods
    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }
}
class StepDuration {
    private String text;
    private float value;

    // Getter Methods
    public String getText() {
        return text;
    }

    public float getValue() {
        return value;
    }
}
class StepDistance {
    private String text;
    private float value;

    // Getter Methods
    public String getText() {
        return text;
    }

    public float getValue() {
        return value;
    }
}
