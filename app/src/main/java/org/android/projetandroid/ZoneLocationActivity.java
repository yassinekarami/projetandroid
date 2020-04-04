package org.android.projetandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.squareup.otto.Subscribe;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchLocationResultEvent;
import org.android.projetandroid.event.SearchMeasurementResultEvent;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.Measurement;
import org.android.projetandroid.service.LocationSearchService;
import org.android.projetandroid.ui.LocationAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZoneLocationActivity extends AppCompatActivity {

    @BindView(R.id.location_recyclerView)
    RecyclerView mLocationRecyclerView;

    @BindView(R.id.location_list_loader)
    ProgressBar mProgressBar;

    @BindView(R.id.location_list_search_edittext)
    EditText mLocationSearch;

    private LocationAdapter mLocationAdapter;

    private Intent intent;
    private List<Location> location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_location);
        ButterKnife.bind(this);

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
            mLocationAdapter = new LocationAdapter(this, new ArrayList<>());
            mLocationRecyclerView.setAdapter(mLocationAdapter);
            mLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mLocationSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    LocationSearchService.INSTANCE.searchLocationFromDB(intent.getStringExtra("zone"), s.toString());
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBusManager.bus.unregister(this);

    }

    @Subscribe
    public void  searchResult(final SearchLocationResultEvent event) {

        // Here someone has posted a SearchResultEvent
        runOnUiThread (() -> {
            // Step 1: Update adapter's model
            mLocationAdapter.setLocations(event.getLocation());
            mLocationAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
            location = event.getLocation();

            for(Location l : event.getLocation()) {
                LocationSearchService.INSTANCE.searchMeasurement(intent.getStringExtra("zone"), l.location);
            }
        });
    }

    @Subscribe
    public void searchResult(final SearchMeasurementResultEvent event) {

  runOnUiThread(() -> {
        HashMap<String, List<Measurement>> measurementHashmap = new HashMap<String, List<Measurement>>();
        List<Measurement> mesureList;

        for(Measurement m : event.getMeasurements())
        {
            if(!measurementHashmap.containsKey(m.city.location)) { // la clée n'existe pas
               mesureList  = new ArrayList<>();
               measurementHashmap.put(m.city.location, mesureList);
            }

            if(m.city.location != null && m.mesurement != null) {
                measurementHashmap.get(m.city.location).add(m);

            }
        }
        if(!measurementHashmap.isEmpty()) {
            mLocationAdapter.setMeasurements(measurementHashmap);
            mLocationAdapter.notifyDataSetChanged();
        }
  });


    }

}
