package org.android.projetandroid.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.android.projetandroid.model.ZoneResult;

import java.lang.reflect.Modifier;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class ZoneSearchService {



    public static ZoneSearchService INSTANCE = new ZoneSearchService();
    private final ZoneSearchRESTService mZoneSearchRESTService;


    public ZoneSearchService() {
        Gson  gsonConverter = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl("https://api.openaq.org/v1/cities?country=FR")
                .addConverterFactory(GsonConverterFactory.create(gsonConverter)).build();

        mZoneSearchRESTService = retrofit.create(ZoneSearchRESTService.class);
    }

    private void searchZone(){
        mZoneSearchRESTService.listzone().enqueue(new Callback<ZoneResult>() {
            @Override
            public void onResponse(Call<ZoneResult> call, Response<ZoneResult> response) {
                if(response.body() != null) {
                    System.out.println("+++++++++++++++++++++++++++++");
                    System.out.println(response.body().features);
                }

            }

            @Override
            public void onFailure(Call<ZoneResult> call, Throwable t) {

            }
        });
    }

    public interface ZoneSearchRESTService {
        @GET("zones/all")
        Call<ZoneResult> listzone();
    }
}


