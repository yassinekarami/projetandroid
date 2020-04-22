package org.android.projetandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnListActivity)
    public void clickedOnSwitchList() {
        Intent intent = new Intent(this, ZoneListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnMapActivity)
    public void clicketOnSwitchMap() {
        Intent intent = new Intent(this, MapZoneActivity.class);
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
}
