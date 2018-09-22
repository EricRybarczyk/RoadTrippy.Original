package ericrybarczyk.me.roadtrippy.endpoints;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchService {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public static final String PLACES_API_TEXT_QUERY_INPUT_TYPE = "textquery";
    public static final String PLACES_API_QUERY_FIELDS = "formatted_address,geometry,icon,id,name,place_id,plus_code,types,photos";

}
