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
    private HashMap<String, String> mMeasurements;
    private  Measurement[] mes = null;
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

        holder.mLocationCityTextView.setText(location.getLocation());
        Location.Indicateur[] indic = gson.fromJson(location.indicateur, Location.Indicateur[].class);

        for(Location.Indicateur i : indic) {  // le nombre de mesure qui a été fait pour chaque paramètre
          //  holder.mLocationParameterTextView.setText(i.getParameter() +" : "+i.getCount()+ "\n");
        }

        if(mMeasurements != null) {
            mes = gson.fromJson(mMeasurements.get(location.getLocation()), Measurement[].class);
            if(mes != null) {
                String mesures = "";
                for(Measurement m : mes) {
                    mesures = mesures +"\n"+ m.parameter+" : "+m.value +" "+m.unit;
                }
               // holder.mLocationMeasurementTextView.append(m.parameter+" : "+m.value +" "+m.unit+"\n");
                holder.mLocationMeasurementTextView.setText(mesures);
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

    public void setMeasurements(HashMap<String, String> measurements) { this.mMeasurements = measurements;}


    class LocationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.location_adapter_parameters)
        TextView mLocationParameterTextView;

        @BindView(R.id.location_adapter_locations)
        TextView mLocationCityTextView;

        @BindView(R.id.location_adapter_measurements)
        TextView mLocationMeasurementTextView;

        @BindView(R.id.location_adapter_detail)
        Button mLocationDetailButton;


        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
