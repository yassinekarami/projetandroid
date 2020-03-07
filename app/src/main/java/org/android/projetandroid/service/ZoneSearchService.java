package org.android.projetandroid.service;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.android.projetandroid.ZoneListActivity;
import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchResultEvent;
import org.android.projetandroid.model.Zone;
import org.android.projetandroid.model.ZoneResult;

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

public class ZoneSearchService {


    private static final long REFRESH_DELAY = 650;
    public static ZoneSearchService INSTANCE = new ZoneSearchService();
    private final ZoneSearchRESTService mZoneSearchRESTService;
    private ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mLastScheduleTask;

    public ZoneSearchService() {
        Gson  gsonConverter = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl("https://api.openaq.org/v1/")
                .addConverterFactory(GsonConverterFactory.create(gsonConverter)).build();

        mZoneSearchRESTService = retrofit.create(ZoneSearchRESTService.class);
    }

    public void searchZone(final String search){
        mZoneSearchRESTService.listzone("FR").enqueue(new Callback<ZoneResult>() {

            @Override
            public void onResponse(Call<ZoneResult> call, Response<ZoneResult> response) {

                if(response.body() != null && response.body().results != null) {
                    ActiveAndroid.beginTransaction();
                    try {
                        for (Zone z : response.body().results) {
                            z.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    }
                    finally{
                        ActiveAndroid.endTransaction();
                        searchZoneFromDB(search);
                    }
                }
            }

            @Override
            public void onFailure(Call<ZoneResult> call, Throwable t) {
                searchZoneFromDB(search);
            }
        });
    }

    public void searchZoneFromDB(String search) {
        if (mLastScheduleTask != null && !mLastScheduleTask.isDone()) {
            mLastScheduleTask.cancel(true);
        }
        mLastScheduleTask = mScheduler.schedule(new Runnable() {
            public void run() {
                List<Zone> matchingZonesFromBD = new Select().from(Zone.class)
                        .where("name LIKE '%" + search + "%'")
                        .orderBy("name")
                        .execute();

                EventBusManager.bus.post(new SearchResultEvent(matchingZonesFromBD));
            }
        }, REFRESH_DELAY, TimeUnit.MILLISECONDS);
    }


    public interface ZoneSearchRESTService {
        @GET("cities")
        Call<ZoneResult> listzone(@Query("country") String country);
    }
}


