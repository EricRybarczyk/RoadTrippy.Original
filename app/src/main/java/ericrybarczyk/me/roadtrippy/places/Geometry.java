package ericrybarczyk.me.roadtrippy.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// This class was generated from Google Maps Places API JSON using http://www.jsonschema2pojo.org/

public class Geometry {

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("viewport")
    @Expose
    private Viewport viewport;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

}
