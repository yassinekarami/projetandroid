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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchLocationResultEvent;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.service.LocationSearchService;
import org.android.projetandroid.ui.LocationAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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


    HashMap<String, String> paramHashMap;

    private LocationAdapter mLocationAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);
        ButterKnife.bind(this);



        mLocationAdapter = new LocationAdapter(this, new ArrayList<>());
        mLocationRecyclerView.setAdapter(mLocationAdapter);
        mLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*

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
                LocationSearchService.INSTANCE.searchRechercheLocationFromDB(mRechercheZone.getText().toString(), s.toString());
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
                LocationSearchService.INSTANCE.searchRechercheLocationFromDB(s.toString(), mRechercheLocation.getText().toString());
            }
        });
         */

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


    @Subscribe
    public void searchResult(final SearchLocationResultEvent event) {

        runOnUiThread(() -> {
            mLocationAdapter.setLocations(event.getLocation());
            mLocationAdapter.notifyDataSetChanged();

            mProgressBar.setVisibility(View.GONE);

        });
    }

    @OnClick(R.id.activity_recherche_btn)
    public void rechercheClick()
    {
        paramHashMap = new HashMap<>();
        paramHashMap.put("pm25",  mPm25Pram.getText().toString());
        paramHashMap.put("pm10",  mPm10Pram.getText().toString());
        paramHashMap.put("so2",  mSo2Pram.getText().toString());
        paramHashMap.put("no2",  mNo2Pram.getText().toString());
        paramHashMap.put("o3",  mO3Pram.getText().toString());
        paramHashMap.put("co",  mCoPram.getText().toString());
        paramHashMap.put("bc",  mBcPram.getText().toString());
        LocationSearchService.INSTANCE.searchRechercheLocationFromDB(
                mRechercheZone.getText().toString(),
                mRechercheLocation.getText().toString(),
                paramHashMap
                );
        mProgressBar.setVisibility(View.VISIBLE);
    }
}
