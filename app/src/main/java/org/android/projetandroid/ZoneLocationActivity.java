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
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchLocationResultEvent;
import org.android.projetandroid.event.SearchResultEvent;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.service.LocationSearchService;
import org.android.projetandroid.service.ZoneSearchService;
import org.android.projetandroid.ui.LocationAdapter;
import org.android.projetandroid.ui.ZoneAdapter;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_location);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent != null  && intent.hasExtra("zone")){
            EventBusManager.bus.register(this);
            LocationSearchService.INSTANCE.searchLocation(intent.getStringExtra("zone"));

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
                    LocationSearchService.INSTANCE.searchLocationFromDB(s.toString());
                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        EventBusManager.bus.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void  searchResult(final SearchLocationResultEvent event) {

        // Here someone has posted a SearchResultEvent
        runOnUiThread (() -> {
            // Step 1: Update adapter's model
            mLocationAdapter.setLocations(event.getLocation());
            mLocationAdapter.notifyDataSetChanged();

            mProgressBar.setVisibility(View.GONE);

        });
    }

}
