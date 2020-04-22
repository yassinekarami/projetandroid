package org.android.projetandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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
import org.android.projetandroid.event.SearchResultEvent;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.Zone;
import org.android.projetandroid.service.ZoneSearchService;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapZoneActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mActiveGoogleMap;

    @BindView(R.id.activity_main_search_adress_edittext)
    EditText mSearchEditText;

    @BindView(R.id.activity_main_loader)
    ProgressBar mProgressBar;

    private Map<String, Location> mMarkersToPlaces = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_map_zone);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getIntent().hasExtra("currentSearch")) {
            mSearchEditText.setText(getIntent().getStringExtra("currentSearch"));
        }

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do when texte is about to change
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // While text is changing, hide list and show loader
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Once text has changed
                // Show a loader
                mProgressBar.setVisibility(View.VISIBLE);

                // Launch a search through the PlaceSearchService
                ZoneSearchService.INSTANCE.searchZone(editable.toString());
            }
        });

       /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

    }

    @Override
    protected void onResume(){
        super.onResume();
        EventBusManager.bus.register(this);
        ZoneSearchService.INSTANCE.searchZone(mSearchEditText.getText().toString());
    }

    @Override
    protected void onPause(){
        EventBusManager.bus.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void searchResult(final SearchLocationResultEvent event){
        //update markers
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Here someone has posted a SearchResultEvent
                // Check that map is ready
                if (mActiveGoogleMap != null) {
                    // Update map's markers
                    mActiveGoogleMap.clear();
                    mMarkersToPlaces.clear();

                    LatLngBounds.Builder cameraBounds = LatLngBounds.builder();
                    for (Location place : event.getLocation()) {
                        int markerIconResource = R.drawable.common_google_signin_btn_icon_dark;
                        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), markerIconResource);
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 50, 50, false);

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(place.coordinates.latitude, place.coordinates.longitude))
                                .title(place.city)
                                .snippet(place.city)
                                .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

                        cameraBounds.include(markerOptions.getPosition());

                        Marker marker = mActiveGoogleMap.addMarker(markerOptions);
                        mMarkersToPlaces.put(marker.getId(), place);
                    }

                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick(R.id.activity_map_switch_button)
    public void clickedOnSwitchToList(){
        Intent switchToListIntent = new Intent (this, MainActivity.class);
        switchToListIntent.putExtra("currentSearch", mSearchEditText.getText().toString());
        startActivity(switchToListIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mActiveGoogleMap = googleMap;
        mActiveGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mActiveGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        mActiveGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Location associatedPlace = mMarkersToPlaces.get(marker.getId());
                if (associatedPlace != null) {
                    Intent seePlaceDetailIntent = new Intent(MapZoneActivity.this, LocationDetailActivity.class);
                    seePlaceDetailIntent.putExtra("placeStreet", associatedPlace.city);
                    startActivity(seePlaceDetailIntent);
                }
            }
        });
    }
}
