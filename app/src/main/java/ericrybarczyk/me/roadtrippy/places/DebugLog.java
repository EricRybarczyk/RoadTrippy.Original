package ericrybarczyk.me.roadtrippy.places;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// This class was generated from Google Maps Places API JSON using http://www.jsonschema2pojo.org/

public class DebugLog {

    @SerializedName("line")
    @Expose
    private List<Object> line = null;

    public List<Object> getLine() {
        return line;
    }

    public void setLine(List<Object> line) {
        this.line = line;
    }

}
