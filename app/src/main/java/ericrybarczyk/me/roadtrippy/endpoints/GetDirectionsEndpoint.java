package ericrybarczyk.me.roadtrippy.endpoints;


import ericrybarczyk.me.roadtrippy.directions.DirectionsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDirectionsEndpoint {

    @GET("directions/json") // ?key={apikey}&origin={origin}&destination={destination}
    Call<DirectionsResponse> getDirections(@Query("key") String apikey, @Query("origin") String origin, @Query("destination") String destination, @Query("alternatives") String alternatives, @Query("avoid") String avoid);

    @GET("directions/json") // ?key={apikey}&origin={origin}&destination={destination}
    Call<DirectionsResponse> getDirections(@Query("key") String apikey, @Query("origin") String origin, @Query("destination") String destination, @Query("alternatives") String alternatives);
}

