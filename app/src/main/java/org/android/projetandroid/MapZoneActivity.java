package org.android.projetandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchLocationResultEvent;
import org.android.projetandroid.event.SearchMeasurementResultEvent;
import org.android.projetandroid.event.SearchMeteoResultEvent;
import org.android.projetandroid.event.SearchResultEvent;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.Measurement;
import org.android.projetandroid.model.Meteo;
import org.android.projetandroid.model.Zone;
import org.android.projetandroid.service.LocationSearchService;
import org.android.projetandroid.service.MeteoSearchService;
import org.android.projetandroid.service.ZoneSearchService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class MapZoneActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mActiveGoogleMap;

    private Intent intent;
    private List<Location> location;


    private Map<String, Location> mMarkersToPlaces = new LinkedHashMap<>();

    private HashMap<String, List<Measurement>> measurementHashmap = new HashMap<String, List<Measurement>>();
    private HashMap<String, String> meteoHashmap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_zone);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        intent = getIntent();
        if(intent != null  && intent.hasExtra("zone")) {
            LocationSearchService.INSTANCE.searchLocation(intent.getStringExtra("zone"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (intent != null && intent.hasExtra("zone")) {
            EventBusManager.bus.register(this);
        }

    }

    @Override
    protected void onPause() {
        // Unregister from Event bus : if event are posted now, the activity will not receive it
        EventBusManager.bus.unregister(this);

        // Do NOT forget to call super.onPause()
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mActiveGoogleMap = googleMap;
        mActiveGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mActiveGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        mActiveGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Location associatedLocation = mMarkersToPlaces.get(marker.getId());
                if (associatedLocation != null) {
                    Intent seeLocationDetailActivity = new Intent(MapZoneActivity.this, LocationDetailActivity.class);
                    seeLocationDetailActivity.putExtra("location", associatedLocation);
                    startActivity(seeLocationDetailActivity);
                }
            }
        });


    }





    // recupération des mesures
    @Subscribe
    public void searchResult(final SearchMeasurementResultEvent event) {
        runOnUiThread(() -> {

            List<Measurement> mesureList;

            for(Measurement m : event.getMeasurements())
            {
                if(!measurementHashmap.containsKey(m.location)) { // la clée n'existe pas
                    mesureList  = new ArrayList<>();
                    measurementHashmap.put(m.location, mesureList);
                }

                if(m.location != null && m.mesure != null) {
                    measurementHashmap.get(m.location).add(m);

                }
            }

        });
    }


    // recupération de la température
    @Subscribe
    public void searchMeteoResult(final SearchMeteoResultEvent event) {
        runOnUiThread(() -> {

            for(Meteo m: event.getMeteos())
            {
                meteoHashmap.put(m.location, m.courant);
            }
        });
    }

    @Subscribe
    public void searchResult(final SearchLocationResultEvent event) {

        runOnUiThread (() -> {
            // Step 1: Update adapter's model
            location = event.getLocation();

            if (mActiveGoogleMap != null) {
                // Update map's markers
                mActiveGoogleMap.clear();
                mMarkersToPlaces.clear();

                LatLngBounds.Builder cameraBounds = LatLngBounds.builder();
                for (Location l : event.getLocation()) {

                    LocationSearchService.INSTANCE.searchMeasurement(intent.getStringExtra("zone"), l.location);
                    MeteoSearchService.INSTANCE.searchTemperature(l);

                    // Step 1: create marker icon (and resize drawable so that marker is not too big)
                    int markerIconResource = R.drawable.location_icon;

                    Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), markerIconResource);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 50, 50, false);

                    // Step 2: define marker options
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(l.coordinates.latitude, l.coordinates.longitude))
                            .title(l.location)
                            .snippet(meteoHashmap.get(l.location))
                            .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

                    // Step 3: include marker in camera bounds
                    cameraBounds.include(markerOptions.getPosition());

                    // Step 4: add marker
                    Marker marker = mActiveGoogleMap.addMarker(markerOptions);
                    mMarkersToPlaces.put(marker.getId(), l);
                }
            }
        });
    }



}
