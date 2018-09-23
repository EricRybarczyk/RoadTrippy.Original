package ericrybarczyk.me.roadtrippy.directions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// This class was generated from Google Maps Places API JSON using http://www.jsonschema2pojo.org/

public class OverviewPolyline {

    @SerializedName("points")
    @Expose
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

}
