package org.android.projetandroid;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.squareup.otto.Subscribe;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchLocationResultEvent;
import org.android.projetandroid.event.SearchResultEvent;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.Zone;
import org.android.projetandroid.service.LocationSearchService;
import org.android.projetandroid.service.ZoneSearchService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapZoneActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mActiveGoogleMap;
    private HashMap<Zone, List<Location>> listeZoneLoc = new HashMap<>();
    private List<Zone> listeZone = new ArrayList<>();
    private List<Location> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_zone);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    protected void onPause() {
        EventBusManager.bus.unregister(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBusManager.bus.register(this);
        ZoneSearchService.INSTANCE.searchZone();
    }

    @Subscribe
    public void  searchResult(final SearchResultEvent event) {
        runOnUiThread (() -> {
            listeZone = event.getZones();
            for(Zone z : listeZone){
                LocationSearchService.INSTANCE.searchLocation(z);
            }
        });
    }

    @Subscribe
    public void searchResult(final SearchLocationResultEvent event) {
        runOnUiThread (() -> {
           locations = event.getLocation();
           listeZoneLoc.put(event.zone, locations);
        });

        for(Zone z : listeZoneLoc.keySet()){
            List<Location> listeLocTemp = listeZoneLoc.get(z);
            int diviseur = listeLocTemp.size();
            double lng = 0d;
            double lat = 0d;


            for(Location location : listeLocTemp){
                lng += location.getCoordinates().getLongitude();
                lat += location.getCoordinates().getLattitude();
            }

            lng = lng/diviseur;
            lat = lat/diviseur;


            List<Double> coordonnees = new ArrayList<>();
            coordonnees.add(lng);
            coordonnees.add(lat);
            z.setCoordonnees(coordonnees);
        }

        //testing
        for(Zone z : listeZoneLoc.keySet()){
            System.out.println("===============");
            System.out.println(z.getCoordonnees());
            System.out.println("===============");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mActiveGoogleMap = googleMap;
    }
}
