package org.android.projetandroid.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.android.projetandroid.LocationDetailActivity;
import org.android.projetandroid.R;
import org.android.projetandroid.ZoneLocationActivity;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.Measurement;
import org.android.projetandroid.model.Meteo;
import org.json.JSONArray;


import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private List<Location> mLocations;

    // string sera le code de la location
    private HashMap<String, List<Measurement> > mMeasurements;
    private HashMap<String, String> mMeteos;
    private  Measurement mes = null;
    Gson gson ;

    public LocationAdapter(Activity context, List<Location> locations){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mLocations = locations;

        gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
    }

    @NonNull
    @Override
    public LocationAdapter.LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.location_item, parent, false);
        LocationAdapter.LocationViewHolder holder = new LocationAdapter.LocationViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.LocationViewHolder holder, int position) {
        final Location location = mLocations.get(position);

        holder.mLocationZoneTextView.setText(location.city);
        holder.mLocationCityTextView.setText(location.location);

        if(mMeasurements != null) {

            if(mMeasurements.get(location.location) != null) {
                String mesures = "";
                for(Measurement m: mMeasurements.get(location.location)) {
                    mesures = mesures +"\n"+ m.mesurement.parameter+" : "+m.mesurement.value +" "+m.mesurement.unit;
                }
                holder.mLocationMeasurementTextView.setText(mesures);
            }
        }

        if(mMeteos != null) {
            if(mMeteos.get(location.location) != null) {

                Meteo.Temperature t = gson.fromJson(mMeteos.get(location.location), Meteo.Temperature.class);
                holder.mLocationMeteoCourante.setText("Temperature du : "+t.date + "  \n "+t.valeur );
            }
        }

        holder.mLocationDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Intent seeLocationDetailActivity = new Intent(context, LocationDetailActivity.class);
                seeLocationDetailActivity.putExtra("location", location);
                context.startActivity(seeLocationDetailActivity);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.mLocations.size();
    }

    public void setLocations(List<Location> locations) {
        this.mLocations = locations;
    }

    public void setMeasurements(HashMap<String, List<Measurement> >measurements) { this.mMeasurements = measurements;}

    public void setMeteo(HashMap<String, String> meteo) {this.mMeteos = meteo;}

    public HashMap<String, List<Measurement>> getMeasurements() { return this.mMeasurements; }


    class LocationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.location_adapter_locations)
        TextView mLocationCityTextView;

        @BindView(R.id.location_adapter_zone)
        TextView mLocationZoneTextView;

        @BindView(R.id.location_adapter_measurements)
        TextView mLocationMeasurementTextView;

        @BindView(R.id.location_adapter_meteo_courante)
        TextView mLocationMeteoCourante;

        @BindView(R.id.location_adapter_detail)
        Button mLocationDetailButton;


        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
