package ericrybarczyk.me.roadtrippy.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// This class was generated from Google Maps Places API JSON using http://www.jsonschema2pojo.org/

public class Viewport {

    @SerializedName("northeast")
    @Expose
    private Northeast northeast;
    @SerializedName("southwest")
    @Expose
    private Southwest southwest;

    public Northeast getNortheast() {
        return northeast;
    }

    public void setNortheast(Northeast northeast) {
        this.northeast = northeast;
    }

    public Southwest getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Southwest southwest) {
        this.southwest = southwest;
    }

}
