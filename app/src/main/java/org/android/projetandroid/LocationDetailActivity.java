package org.android.projetandroid;

import android.os.Bundle;
import android.content.Intent;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Update;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchMeasurementResultEvent;
import org.android.projetandroid.event.SearchMeteoResultEvent;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.Measurement;
import org.android.projetandroid.model.Meteo;
import org.android.projetandroid.service.LocationSearchService;
import org.android.projetandroid.service.MeteoSearchService;

import java.lang.reflect.Modifier;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationDetailActivity extends AppCompatActivity {

    Gson gson ;

    @BindView(R.id.detail_location_favoris)
    Button mFavorisButton;

    @BindView(R.id.detail_location_favoris_retirer)
    Button mButtonRetirer;

    @BindView(R.id.detail_location_indicateur)
    TextView mDetailIndicateur;

    @BindView(R.id.detail_location_locations)
    TextView mDetailLocations;

    @BindView(R.id.detail_location_measurements)
    TextView mMeasurementLocations;

    @BindView(R.id.detail_location_meteo_prevision)
    TextView mDetailLocationPrevision;

    @BindView(R.id.detail_location_image)
    ImageView mDetailImage;

    private Location locationDetail;
    String coordonne;

    //Librarie a utilisé
    //https://square.github.io/picasso/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        ButterKnife.bind(this);


        this.gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();

        mDetailLocationPrevision.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        locationDetail = (Location)intent.getSerializableExtra("location");

        EventBusManager.bus.register(this);
        LocationSearchService.INSTANCE.searchMeasurementFromDB(locationDetail.location);
        MeteoSearchService.INSTANCE.searchMeteoFromDB(locationDetail);

        // si la location est en favoris
        if (locationDetail.favoris) {
            this.mFavorisButton.setVisibility(View.INVISIBLE);
            this.mButtonRetirer.setVisibility(View.VISIBLE);
        }
        // la location n'est pas en favoris
        else {

            this.mFavorisButton.setVisibility(View.VISIBLE);
            this.mButtonRetirer.setVisibility(View.INVISIBLE);
        }

        mDetailLocations.append(locationDetail.location);

        this.coordonne = this.locationDetail.coordinates.latitude+","+this.locationDetail.coordinates.longitude;
        Picasso.get().load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location="+this.coordonne+"&fov=80&heading=70&pitch=&key=AIzaSyDWg17olhB-Wq9v5Cfg5a2YrmZSP7fhuvM")
                .into(mDetailImage);


    }

    @Override
    protected void onPause() {
        EventBusManager.bus.unregister(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.detail_location_favoris)
    public void addToFavoris() {
        ActiveAndroid.beginTransaction();
        try {
            new Update(Location.class).set("favoris = 1")
                    .where("location =  '"+locationDetail.location+"'")
                    .execute();

            Toast.makeText(getApplicationContext(), "Location ajouté aux favoris", Toast.LENGTH_SHORT).show();

        }finally {
            ActiveAndroid.setTransactionSuccessful();
            this.mFavorisButton.setVisibility(View.INVISIBLE);
            this.mButtonRetirer.setVisibility(View.VISIBLE);
        }
        ActiveAndroid.endTransaction();

    }


    @OnClick(R.id.detail_location_favoris_retirer)
    public void removeFromFavoris() {
        ActiveAndroid.beginTransaction();
        try {
            new Update(Location.class).set("favoris = 0")
                    .where("location =  '"+locationDetail.location+"'")
                    .execute();

            Toast.makeText(getApplicationContext(), "Location retiré aux favoris", Toast.LENGTH_SHORT).show();
        }finally {
            ActiveAndroid.setTransactionSuccessful();
            this.mFavorisButton.setVisibility(View.VISIBLE);
            this.mButtonRetirer.setVisibility(View.INVISIBLE);
        }
        ActiveAndroid.endTransaction();
    }

    @Subscribe
    public void  searchResult(final SearchMeasurementResultEvent event) {
        runOnUiThread( () -> {

            for(Measurement m: event.getMeasurements()) {
                Measurement.Values[] valeur = gson.fromJson(m.mesure, Measurement.Values[].class);
                for (Measurement.Values v : valeur)
                {
                    mMeasurementLocations.append(v.parameter+" : "+v.value +" "+v.unit+"\n");
                }
            }
        });

    }

    @Subscribe
    public void searchMeteoResult(final SearchMeteoResultEvent event) {
        runOnUiThread( () -> {
            mDetailLocationPrevision.append("Prevision 7 jours \n");
            for(Meteo m : event.getMeteos()) {
                Meteo.Temperature[] temp = gson.fromJson(m.prevision, Meteo.Temperature[].class);
                for(Meteo.Temperature t: temp) {
                    mDetailLocationPrevision.append("   " +t.date+ " : " +t.valeur+"\n");
                }
            }
        });

    }
}
