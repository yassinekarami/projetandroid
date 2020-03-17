package org.android.projetandroid;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.otto.Subscribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchLocationResultEvent;
import org.android.projetandroid.service.LocationSearchService;
import org.android.projetandroid.ui.LocationAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavorisActivity extends AppCompatActivity {

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
            }
        });
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
        LocationSearchService.INSTANCE.searchFavoris();
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
