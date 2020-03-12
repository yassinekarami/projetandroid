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

import org.android.projetandroid.R;
import org.android.projetandroid.model.Location;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private List<Location> mLocations;

    public LocationAdapter(Activity context, List<Location> locations){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mLocations = locations;
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
        for (String l : location.getLocations()) {
            holder.mLocationCityTextView.append(l+"\n");
        }
        for(Location.Indicateur i : location.getCountsByMeasurement()) {
            holder.mLocationParameterTextView.setText(holder.mLocationParameterTextView.getText() + i.getParameter() +" : "+i.getCount()+ "\n");
        }

    }

    @Override
    public int getItemCount() {
        return this.mLocations.size();
    }

    public void setLocations(List<Location> locations) {
        this.mLocations = locations;
    }


    class LocationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.location_adapter_parameters)
        TextView mLocationParameterTextView;

        @BindView(R.id.location_adapter_locations)
        TextView mLocationCityTextView;


        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
