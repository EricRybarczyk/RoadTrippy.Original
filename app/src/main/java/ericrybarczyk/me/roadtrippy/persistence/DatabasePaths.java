package ericrybarczyk.me.roadtrippy.persistence;

public class DatabasePaths {
    public static final String BASE_PATH_TRIPS = "trips/";
    public static final String BASE_PATH_TRIPDAYS = "tripdays/";
    public static final String BASE_PATH_USERS = "users/";
    public static final String BASE_PATH_ARCHIVE = "tripArchive/";

    public static final String KEY_TRIP_LIST_DEFAULT_SORT = "departureDate";
    public static final String KEY_TRIP_LIST_IS_ARCHIVED = "isArchived";

    public static final String KEY_TRIPDAY_HIGHLIGHT_CHILD = "isHighlight";
    public static final String KEY_TRIPDAY_DESTINATIONS_CHILD = "destinations";

    public static final String KEY_ARCHIVE_TRIPS_CHILD = "archivedTrips";
    public static final String KEY_ARCHIVE_TRIPDAYS_CHILD = "archivedTripDays";
}
