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
import org.android.projetandroid.event.SearchResultEvent;
import org.android.projetandroid.service.ZoneSearchService;
import org.android.projetandroid.ui.ZoneAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZoneListActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.activity_list_search_edittext)
    EditText mSeachEditText;

    @BindView(R.id.activity_list_loader)
    ProgressBar mProgressBar;

    private ZoneAdapter mZoneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        // effectue un appel REST pour récupéré les zones
        ZoneSearchService.INSTANCE.searchZone();

        mZoneAdapter = new ZoneAdapter(this, new ArrayList<>());
        mRecyclerView.setAdapter(mZoneAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSeachEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mProgressBar.setVisibility(View.VISIBLE);
                ZoneSearchService.INSTANCE.searchZoneFromDB(s.toString());
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
        ZoneSearchService.INSTANCE.searchZone();
    }

    @Subscribe
    public void  searchResult(final SearchResultEvent event) {

        // Here someone has posted a SearchResultEvent
        runOnUiThread (() -> {
            // Step 1: Update adapter's model
            mZoneAdapter.setZones(event.getZones());
            mZoneAdapter.notifyDataSetChanged();

            mProgressBar.setVisibility(View.GONE);

        });
    }



}
