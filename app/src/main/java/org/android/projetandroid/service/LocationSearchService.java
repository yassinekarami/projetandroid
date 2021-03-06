package org.android.projetandroid.service;


import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchLocationResultEvent;
import org.android.projetandroid.event.SearchMeasurementResultEvent;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.LocationResult;
import org.android.projetandroid.model.Measurement;
import org.android.projetandroid.model.MeasurementResult;


import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.HashSet;

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

    // liste pour enregistrer toute les valeurs des mesures
    private List<Measurement.Values> mesList = new ArrayList<>();

    private String locName = "";

    private Gson gson ;

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

                            loc.favoris = false;

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

    public void searchMeasurement(final String zone, final String location) {
        mLocationSearchRESTService.listMeasurement("FR", zone, location).enqueue(new Callback<MeasurementResult>() {
            @Override
            public void onResponse(Call<MeasurementResult> call, Response<MeasurementResult> response) {
                if(response.body() != null && response.body().results != null) {
                    ActiveAndroid.beginTransaction();
                 //   searchMeasurementFromDB(location);
                    try{
                        for (Measurement m : response.body().results) {
                            Measurement mesure = new Measurement();
                            mesure.location = m.location;
                            mesure.city = m.city;
                            List<Measurement.Values> valeur = new ArrayList<>();
                            for (Measurement.Values v :m.measurements) {
                                valeur.add(v);
                            }

                            mesure.mesure = gson.toJson(valeur);
                            mesure.loc = new Select().from(Location.class).where("location=?", m.location).executeSingle();
                            mesure.save();


                        }
                        ActiveAndroid.setTransactionSuccessful();
                    }finally {
                        ActiveAndroid.endTransaction();
                        searchMeasurementFromDB(location);
                    }
                }
            }

            @Override
            public void onFailure(Call<MeasurementResult> call, Throwable t) {
                searchMeasurementFromDB(location);
            }
        });
    }

    public void searchMeasurementFromDB(String location) {

        List<Measurement> matchingMeasurementFromDB = new Select()
                .from(Measurement.class)
                .where("location =?", location)
                .execute();

        EventBusManager.bus.post(new SearchMeasurementResultEvent(matchingMeasurementFromDB));

    }

    public void searchRechercheMeasurementFromDB(String zone, String location) {

        List<Measurement> matchingMeasurementFromDB = new Select()
                .from(Measurement.class)
                .where("city LIKE '%" + zone +"%'" )
                .where("location LIKE '%" + location +"%'" )
                .execute();


        EventBusManager.bus.post(new SearchMeasurementResultEvent(matchingMeasurementFromDB));

    }


    // recherche des locations pour l'activité LOCATION
    public void searchLocationFromDB(String zone, String search) {
        if (mLastScheduleTask != null && !mLastScheduleTask.isDone()) {
            mLastScheduleTask.cancel(true);
        }
        mLastScheduleTask = mScheduler.schedule(new Runnable() {
            public void run() {

                 List<Location> matchingLocationFromBD = new Select().from(Location.class)
                        .where("zone = ? ", zone)
                        .where("location LIKE '%" + search +"%'" )
                        .orderBy("location")
                        .execute();

                EventBusManager.bus.post(new SearchLocationResultEvent(matchingLocationFromBD));
            }
        }, REFRESH_DELAY, TimeUnit.MILLISECONDS);
    }

    public void searchFavoris() {
        if (mLastScheduleTask != null && !mLastScheduleTask.isDone()) {
            mLastScheduleTask.cancel(true);
        }
        mLastScheduleTask = mScheduler.schedule(new Runnable() {
            public void run() {
                List<Location> matchingLocationFromBD = new Select().from(Location.class)
                        .where("favoris = 1")
                        .orderBy("location")
                        .execute();

                EventBusManager.bus.post(new SearchLocationResultEvent(matchingLocationFromBD));
            }
        }, REFRESH_DELAY, TimeUnit.MILLISECONDS);
    }




    public interface LocationSearchRESTService {
        @GET("locations")
        Call<LocationResult> listLocation(@Query("country") String country, @Query("city") String zone);

        @GET("latest")
        Call<MeasurementResult>listMeasurement(@Query("country") String country, @Query("city") String zone, @Query("location") String location);

    }
}
