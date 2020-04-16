package org.android.projetandroid;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.android.projetandroid.event.EventBusManager;
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
import butterknife.OnClick;

public class RechercheActivty extends AppCompatActivity {


    @BindView(R.id.activity_recherche_zone_edittext)
    EditText mRechercheZone;

    @BindView(R.id.activity_recherche_location_edittext)
    EditText mRechercheLocation;

    @BindView(R.id.activity_recherche_loader)
    ProgressBar mProgressBar;

    @BindView(R.id.location_recyclerView)
    RecyclerView mLocationRecyclerView;

    @BindView(R.id.activity_recherche_btn)
    Button mRechercheButton;

    //paramètre de recherche
    @BindView(R.id.activity_pm25_edittext)
    EditText mPm25Pram;

    @BindView(R.id.activity_pm10_edittext)
    EditText mPm10Pram;

    @BindView(R.id.activity_so2_edittext)
    EditText mSo2Pram;

    @BindView(R.id.activity_no2_edittext)
    EditText mNo2Pram;

    @BindView(R.id.activity_o3_edittext)
    EditText mO3Pram;

    @BindView(R.id.activity_co_edittext)
    EditText mCoPram;

    @BindView(R.id.activity_bc_edittext)
    EditText mBcPram;


    HashMap<String, Float> paramHashMap;
    private LocationAdapter mLocationAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);
        ButterKnife.bind(this);



        mLocationAdapter = new LocationAdapter(this, new ArrayList<>());
        mLocationRecyclerView.setAdapter(mLocationAdapter);
        mLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    @OnClick(R.id.activity_recherche_btn)
    public void rechercheClick()
    {
        paramHashMap = new HashMap<>();

        if(!mPm25Pram.getText().toString().equals("")) {
            paramHashMap.put("pm25", Float.parseFloat(mPm25Pram.getText().toString()));
        }
        else   paramHashMap.put("pm25", 0f);

        if(!mPm10Pram.getText().toString().equals("")) {
            paramHashMap.put("pm10", Float.parseFloat(mPm10Pram.getText().toString()));
        }
        else paramHashMap.put("pm10", 0f);

        if(!mSo2Pram.getText().toString().equals("")){
            paramHashMap.put("so2",  Float.parseFloat(mSo2Pram.getText().toString()));
        }
        else paramHashMap.put("so2", 0f);

        if(!mNo2Pram.getText().toString().equals("")){
            paramHashMap.put("no2",  Float.parseFloat(mNo2Pram.getText().toString()));
        }
        else paramHashMap.put("no2", 0f);

        if(!mO3Pram.getText().toString().equals("")){
            paramHashMap.put("o3",   Float.parseFloat(mO3Pram.getText().toString()));
        }
        else paramHashMap.put("o3", 0f);

        if(!mCoPram.getText().toString().equals("")){
            paramHashMap.put("co",   Float.parseFloat(mCoPram.getText().toString()));
        }
        else paramHashMap.put("co", 0f);

        if(!mBcPram.getText().toString().equals("")){
            paramHashMap.put("bc",   Float.parseFloat(mBcPram.getText().toString()));
        }
        else paramHashMap.put("bc", 0f);

        LocationSearchService.INSTANCE.searchRechercheMeasurementFromDB(
                mRechercheZone.getText().toString(),
                mRechercheLocation.getText().toString()
                );
    }


    @Subscribe
    public void  searchResult(final SearchMeasurementResultEvent event) {

        // Here someone has posted a SearchResultEvent
        HashMap<String, List<Measurement>> measurementHashmap = new HashMap<String, List<Measurement>>();
        List<Measurement> mesureList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();
        for(Measurement mes : event.getMeasurements()) {

            boolean ok = true;
            for(Measurement m: event.getMeasurements()) { // boucle pour parcourir les mesures
                if(paramHashMap.get(m.mesurement.parameter) > m.mesurement.value) { ok = false;}

                if(!measurementHashmap.containsKey(m.city.location)) { // la clée n'existe pas
                    mesureList  = new ArrayList<>();
                    measurementHashmap.put(m.city.location, mesureList);
                }

                if(m.city.location != null && m.mesurement != null) {
                    measurementHashmap.get(m.city.location).add(m);
                }
            }
            if(ok) {
                locationList.add(mes.city);
            }
            else {
                measurementHashmap.remove(mes.city.location);
            }
        }

        runOnUiThread (() -> {

            if(!measurementHashmap.isEmpty()) {
                mLocationAdapter.setMeasurements(measurementHashmap);
            }

            mLocationAdapter.setLocations(locationList);
            mLocationAdapter.notifyDataSetChanged();
        });
    }
}


