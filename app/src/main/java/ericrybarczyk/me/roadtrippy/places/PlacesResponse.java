package ericrybarczyk.me.roadtrippy.places;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// This class was generated from Google Maps Places API JSON using http://www.jsonschema2pojo.org/

public class PlacesResponse {

    @SerializedName("candidates")
    @Expose
    private List<Candidate> candidates = null;
    @SerializedName("debug_log")
    @Expose
    private DebugLog debugLog;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public DebugLog getDebugLog() {
        return debugLog;
    }

    public void setDebugLog(DebugLog debugLog) {
        this.debugLog = debugLog;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
