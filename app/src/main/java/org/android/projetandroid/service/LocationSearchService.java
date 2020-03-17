package org.android.projetandroid.service;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchLocationResultEvent;
import org.android.projetandroid.event.SearchResultEvent;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.LocationResult;
import org.android.projetandroid.model.Zone;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class LocationSearchService {


    private static final long REFRESH_DELAY = 650;
    public static LocationSearchService INSTANCE = new LocationSearchService();
    private final LocationSearchService.LocationSearchRESTService mLocationSearchRESTService;
    private ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mLastScheduleTask;

    Gson gson ;

    public LocationSearchService() {
        Gson gsonConverter = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl("https://api.openaq.org/v1/")
                .addConverterFactory(GsonConverterFactory.create(gsonConverter)).build();

        mLocationSearchRESTService = retrofit.create(LocationSearchRESTService.class);

        gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
    }

    // recupère les locations de la zone passé en paramètre
    public void searchLocation(final String zone) {
        mLocationSearchRESTService.listLocation("FR", zone).enqueue(new Callback<LocationResult>() {
            @Override
            public void onResponse(Call<LocationResult> call, Response<LocationResult> response) {
                if(response.body() != null && response.body().results != null) {
                    ActiveAndroid.beginTransaction();
                    try {
                        for(Location l : response.body().results) {

                            Location loc = new Location();
                            loc.location = l.location;
                            loc.city = l.city;

                            Location.Coordinates c = new Location.Coordinates();
                            c.latitude = l.coordinates.latitude;
                            c.longitude = l.coordinates.longitude;
                            loc.coordinates = c;
                            loc.coordinates.save();

                            loc.indicateur = gson.toJson(l.countsByMeasurement);
                            loc.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    }
                    finally {
                        ActiveAndroid.endTransaction();
                        searchLocationFromDB(zone, "");
                    }
                }
            }

            @Override
            public void onFailure(Call<LocationResult> call, Throwable t) {
                searchLocationFromDB(zone, "");
            }
        });
    }


    public void searchLocationFromDB(String zone, String search) {
        if (mLastScheduleTask != null && !mLastScheduleTask.isDone()) {
            mLastScheduleTask.cancel(true);
        }
        mLastScheduleTask = mScheduler.schedule(new Runnable() {
            public void run() {
                List<Location> matchingLocationFromBD = new Select().from(Location.class)
                        .where("zone LIKE '%" + zone + "%'")
                        .where("location LIKE '%" + search + "%'")
                        .orderBy("location")
                        .execute();

                EventBusManager.bus.post(new SearchLocationResultEvent(matchingLocationFromBD));
            }
        }, REFRESH_DELAY, TimeUnit.MILLISECONDS);
    }

    public interface LocationSearchRESTService {
        @GET("locations")
        Call<LocationResult> listLocation(@Query("country") String country, @Query("city") String zone);

    }
}
