package org.android.projetandroid.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchLocationResultEvent;
import org.android.projetandroid.event.SearchResultEvent;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.LocationResult;

import java.lang.reflect.Modifier;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

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
    }

    public void searchLocation(final String location) {
        mLocationSearchRESTService.listLocation(location).enqueue(new Callback<LocationResult>() {
            @Override
            public void onResponse(Call<LocationResult> call, Response<LocationResult> response) {
                if(response.body() != null && response.body().results != null) {
                    EventBusManager.bus.post(new SearchLocationResultEvent(response.body().results));
                }
            }

            @Override
            public void onFailure(Call<LocationResult> call, Throwable t) {

            }
        });
    }

    public interface LocationSearchRESTService {
        @GET("locations")
        Call<LocationResult> listLocation(@Query("city") String location);

    }
}