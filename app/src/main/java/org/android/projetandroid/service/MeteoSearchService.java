package org.android.projetandroid.service;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchLocationResultEvent;
import org.android.projetandroid.event.SearchMeteoResultEvent;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.Meteo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MeteoSearchService {

    private static final long REFRESH_DELAY = 650;
    public static MeteoSearchService INSTANCE = new MeteoSearchService();
    private final MeteoSearchService.MeteoSearchRESTService mMeteoSearchRESTService;
    private ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mLastScheduleTask;

    private Gson gson ;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
    Date date = new Date();
    Calendar c = Calendar.getInstance();

    List<Meteo.Temperature> prevision = new ArrayList<>();


    public MeteoSearchService() {
        Gson gsonConverter = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl("https://www.infoclimat.fr/public-api/gfs/")
                .addConverterFactory(GsonConverterFactory.create(gsonConverter)).build();

        mMeteoSearchRESTService = retrofit.create(MeteoSearchService.MeteoSearchRESTService .class);

        gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
    }


    // recupère les previons des locations passer en parametre
    public void searchTemperature(final Location location) {
        String coordonne = location.coordinates.latitude+","+location.coordinates.longitude;
        mMeteoSearchRESTService.listTemperature(coordonne, "Bx1RRgJ8BCZfclJlUiRQeVA4BTALfVB3B3sLaA5rVisGbVMyA2MBZwVrVisCLQs9BCkPbA02UmJXPAd%2FCHoEZQdtUT0CaQRjXzBSN1J9UHtQfgVkCytQdwdlC2sOZVYrBmRTMgNgAX0Fa1YyAjsLIQQ2D28NNlJ1VysHYQhgBG8HYlE8AmIEYF8yUjRSZlB7UHwFYAthUGEHYgs%2BDmpWZwZhUzQDZwE2BWpWNAI1CyEEMg9mDTNSYlc9B2cIZwRmB3tRKgIYBBVfLVJwUiBQMVAlBXgLYVA2BzA%3D",
                "53ea460b691c955bb24f1fe18ffc268f")
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //c.add(Calendar.DATE, 4); -incrementation de la date
                try{
                    // l'object méteo qui sera enregistré en base de donnée
                    Meteo meteo = new Meteo();


                    // parsing file "JSONExample.json"
                    if(response.body() != null) {
                        Object obj = new JSONParser().parse(response.body().string());

                        // typecasting obj to JSONObject
                        JSONObject jo = (JSONObject) obj;

                        Date dt = new Date();

                        // mise en place de la date du jour
                        c.setTime(dt);
                        c.set(Calendar.HOUR_OF_DAY, 23);
                        c.set(Calendar.MINUTE, 0);
                        c.set(Calendar.SECOND, 0);
                        dt = c.getTime();

                        Map date = ((Map)jo.get(dateFormat.format(dt)));

                        // iterating address Map
                        //TEMPERATURE COURANTE
                        Iterator<Map.Entry> itr1 = date.entrySet().iterator();
                        while (itr1.hasNext()) {
                            Map.Entry pair = itr1.next();
                            if(pair.getKey().equals("temperature")) {
                                Meteo.Temperature t = new Meteo.Temperature();
                                t.date = dateFormat.format(c.getTime());
                                t.valeur = pair.getValue();
                                // convertion de l'objet en JSON
                                // update de meteo
                                meteo.location = location;
                                meteo.courant = gson.toJson(t);

                                prevision.add(t);

                            }
                        }

                        c.add(Calendar.DATE, 1);
                        getMeteo(((Map)jo.get(dateFormat.format(c.getTime()))), location);




                        c.add(Calendar.DATE, 2);
                        getMeteo(((Map)jo.get(dateFormat.format(c.getTime()))), location);


                        c.add(Calendar.DATE, 3);
                        getMeteo(((Map)jo.get(dateFormat.format(c.getTime()))), location);


                        c.add(Calendar.DATE, 4);
                        getMeteo(((Map)jo.get(dateFormat.format(c.getTime()))), location);

                        c.add(Calendar.DATE, 5);
                        getMeteo(((Map)jo.get(dateFormat.format(c.getTime()))), location);

                        c.add(Calendar.DATE, 6);
                        getMeteo(((Map)jo.get(dateFormat.format(c.getTime()))), location);

                        meteo.prevision = gson.toJson(prevision);
                        meteo.save();

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                searchMeteoFromDB(location);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                searchMeteoFromDB(location);
            }
        });
    }

    private void getMeteo(Map date, Location location) {
        if(date != null)
        {
            Iterator<Map.Entry> itr1 = date.entrySet().iterator();
            // iterating address Map
            itr1 = date.entrySet().iterator();
            while (itr1.hasNext()) {
                Map.Entry pair = itr1.next();
                if(pair.getKey().equals("temperature")) {
                    Meteo.Temperature t = new Meteo.Temperature();
                    t.date = dateFormat.format(c.getTime());
                    t.valeur = pair.getValue();


                    // convertion de l'objet en JSON
                    // update de meteo

                    Meteo m = new Meteo();
                    m.location = location;
                    m.courant = gson.toJson(t);

                    prevision.add(t);
                }
            }
        }

    }

    public interface MeteoSearchRESTService {
        @GET("json")
        Call<ResponseBody> listTemperature(@Query(value = "_ll", encoded = true) String coordonne,
                                          @Query(value = "_auth", encoded = true) String auth,
                                          @Query(value = "_c", encoded = true) String key);
    }


    public void searchMeteoFromDB(Location location) {
        if (mLastScheduleTask != null && !mLastScheduleTask.isDone()) {
            mLastScheduleTask.cancel(true);
        }
        mLastScheduleTask = mScheduler.schedule(new Runnable() {
            public void run() {
                List<Meteo> matchingMeteoFromBD = new Select().from(Meteo.class)
                        .join(Location.class)
                        .on("Meteo.location=Locations.Id")
                        .where("Locations.Id = ? ", location.getId())
                        .execute();

                EventBusManager.bus.post(new SearchMeteoResultEvent(matchingMeteoFromBD));
            }
        }, REFRESH_DELAY, TimeUnit.MILLISECONDS);
    }




}
