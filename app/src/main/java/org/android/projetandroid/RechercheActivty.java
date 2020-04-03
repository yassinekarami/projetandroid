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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchLocationResultEvent;
import org.android.projetandroid.service.LocationSearchService;
import org.android.projetandroid.ui.LocationAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class RechercheActivty extends AppCompatActivity {


    @BindView(R.id.activity_recherche_zone_edittext)
    EditText mRechercheZone;

    @BindView(R.id.activity_recherche_location_edittext)
    EditText mRechercheLocation;

    @BindView(R.id.activity_recherche_loader)
    ProgressBar mProgressBar;

    @BindView(R.id.location_recyclerView)
    RecyclerView mLocationRecyclerView;

    @BindView(R.id.activity_recherche_param)
    Spinner mMeasurementParam;


    @BindView(R.id.activity_recherche_param_value)
    EditText mRechercheParamValue;

    private LocationAdapter mLocationAdapter;
    private String[] items;
    ArrayAdapter<String> adapter;
    private String selectedParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);
        ButterKnife.bind(this);

        // les éléments qui seront affiché dan le dropdown
        items = new String[]{"pm25", "pm10", "so2", "no2", "o3", "co", "bc"};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mMeasurementParam.setAdapter(adapter);
        selectedParam = adapter.getItem(0);

        mLocationAdapter = new LocationAdapter(this, new ArrayList<>());
        mLocationRecyclerView.setAdapter(mLocationAdapter);
        mLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        LocationSearchService.INSTANCE.searchRechercheLocationFromDB(mRechercheZone.getText().toString(), mRechercheLocation.getText().toString() ,selectedParam, mRechercheParamValue.getText().toString());
        mRechercheLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mProgressBar.setVisibility(View.VISIBLE);
                LocationSearchService.INSTANCE.searchRechercheLocationFromDB(mRechercheZone.getText().toString(), s.toString(), selectedParam, mRechercheParamValue.getText().toString());
            }
        });

        mRechercheZone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mProgressBar.setVisibility(View.VISIBLE);
                LocationSearchService.INSTANCE.searchRechercheLocationFromDB(s.toString(), mRechercheLocation.getText().toString(), selectedParam, mRechercheParamValue.getText().toString());
            }
        });


        mRechercheParamValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mProgressBar.setVisibility(View.VISIBLE);
                LocationSearchService.INSTANCE.searchRechercheLocationFromDB(mRechercheZone.getText().toString(), mRechercheLocation.getText().toString(), selectedParam, s.toString());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBusManager.bus.register(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBusManager.bus.unregister(this);
    }


    // recupère la valeur du paramètre selectionner par l'utilisateur
    @OnItemSelected(R.id.activity_recherche_param)
    public void onSpinnerItemSelected(int index){
       selectedParam = adapter.getItem(index);
    }


    @Subscribe
    public void searchResult(final SearchLocationResultEvent event) {

        runOnUiThread(() -> {
            mLocationAdapter.setLocations(event.getLocation());
            mLocationAdapter.notifyDataSetChanged();

            mProgressBar.setVisibility(View.GONE);

        });
    }
}
