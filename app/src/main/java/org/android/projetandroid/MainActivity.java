package org.android.projetandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.service.ZoneSearchService;
import org.android.projetandroid.ui.ZoneAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private ZoneAdapter mZoneAdapter;

    @BindView(R.id.activity_main_search_adress_edittext)
    EditText mSearchEditText;

    @BindView(R.id.activity_main_loader)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mZoneAdapter = new ZoneAdapter(this, new ArrayList<>());
        mRecyclerView.setAdapter(mZoneAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().hasExtra("currentSearch")) {
            mSearchEditText.setText(getIntent().getStringExtra("currentSearch"));
        }

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
}

            @Override
            public void afterTextChanged(Editable editable) {
                mProgressBar.setVisibility(View.VISIBLE);
                ZoneSearchService.INSTANCE.searchZone(editable.toString());
            }
        });
    }

    @OnClick(R.id.btnListActivity)
    public void clickedOnSwitchList() {
        Intent intent = new Intent(this, ZoneListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnMapActivity)
    public void clicketOnSwitchMap() {
        Intent intent = new Intent(this, MapZoneActivity.class);
        intent.putExtra("currentSearch", mSearchEditText.getText().toString());
        startActivity(intent);
    }

    @OnClick(R.id.btnFavorisActivity)
    public void clickedOnFavorisList(){
        Intent intent = new Intent (this, FavorisActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnRechercheActivity)
    public void clickedOnRecherche() {
        Intent intent = new Intent(this, RechercheActivty.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBusManager.bus.register(this);
        ZoneSearchService.INSTANCE.searchZone(mSearchEditText.getText().toString());
    }

    @Override
    protected void onPause() {
        EventBusManager.bus.unregister(this);
        super.onPause();
    }
}
