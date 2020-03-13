package org.android.projetandroid;

import android.os.Bundle;
import android.content.Intent;

import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.android.projetandroid.model.Location;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationDetailActivity extends AppCompatActivity {

    @BindView(R.id.detail_location_favoris)
    Button mFavorisButton;

    @BindView(R.id.detail_location_indicateur)
    TextView mDetailIndicateur;

    @BindView(R.id.detail_location_locations)
    TextView mDetailLocations;

    @BindView(R.id.detail_location_image)
    ImageView mDetailImage;

    private  Location location;
    String coordonne;

    //Librarie a utilisé
    //https://square.github.io/picasso/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        location = (Location)intent.getSerializableExtra("location");

        for (Location.Indicateur i : location.getCountsByMeasurement()) {
            mDetailIndicateur.append(i.getParameter() +" : "+i.getCount()+ "\n");
        }

        for(String l: location.getLocations()) {
            mDetailLocations.append(l + "\n");
        }

        this.coordonne = this.location.getCoordinates().getLattitude()+","+this.location.getCoordinates().getLongitude();
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


}
