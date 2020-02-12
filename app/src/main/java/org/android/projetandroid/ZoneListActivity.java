package org.android.projetandroid;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.otto.Subscribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchResultEvent;
import org.android.projetandroid.service.ZoneSearchService;
import org.android.projetandroid.ui.ZoneAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZoneListActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private ZoneAdapter mZoneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        EventBusManager.bus.register(this);

        ZoneSearchService.INSTANCE.searchZone();

        mZoneAdapter = new ZoneAdapter(this, new ArrayList<>());
        mRecyclerView.setAdapter(mZoneAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Subscribe
    public void  searchResult(final SearchResultEvent event) {

        // Here someone has posted a SearchResultEvent
        runOnUiThread (() -> {
            // Step 1: Update adapter's model
            mZoneAdapter.setZones(event.getZones());
            mZoneAdapter.notifyDataSetChanged();


        });

    }

}
