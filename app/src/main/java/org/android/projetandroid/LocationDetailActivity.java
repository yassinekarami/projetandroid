package org.android.projetandroid;

import android.os.Bundle;
import android.content.Intent;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Update;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.Measurement;

import java.lang.reflect.Modifier;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationDetailActivity extends AppCompatActivity {

    Gson gson ;
    String measurements;

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

        gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();

        Intent intent = getIntent();
        locationDetail = (Location)intent.getSerializableExtra("location");
        measurements = intent.getStringExtra("measurements");

        if(measurements != null) {

            Measurement[] locationMeasurements = gson.fromJson(measurements, Measurement[].class);
            for(Measurement m : locationMeasurements) {
              mMeasurementLocations.append(m.parameter+" : "+m.value +" "+m.unit+"\n");
            }
        }

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

        Location.Indicateur[] indic = gson.fromJson(locationDetail.indicateur, Location.Indicateur[].class);

        for(Location.Indicateur i : indic) {
            mDetailIndicateur.append(i.parameter +" : "+i.count+ "\n");
        }
        mDetailLocations.append(locationDetail.location);

        this.coordonne = this.locationDetail.getCoordinates().getLatitude()+","+this.locationDetail.getCoordinates().getLongitude();
        Picasso.get().load("https://maps.googleapis.com/maps/api/streetview?size=400x400&location="+this.coordonne+"&fov=80&heading=70&pitch=&key=AIzaSyDWg17olhB-Wq9v5Cfg5a2YrmZSP7fhuvM")
                .into(mDetailImage);


    }

    @Override
    protected void onPause() {
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
                    .where("identifiant = "+locationDetail.identifiant)
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
                    .where("identifiant = "+locationDetail.identifiant)
                    .execute();

            Toast.makeText(getApplicationContext(), "Location retiré aux favoris", Toast.LENGTH_SHORT).show();
        }finally {
            ActiveAndroid.setTransactionSuccessful();
            this.mFavorisButton.setVisibility(View.VISIBLE);
            this.mButtonRetirer.setVisibility(View.INVISIBLE);
        }
        ActiveAndroid.endTransaction();



    }
}
