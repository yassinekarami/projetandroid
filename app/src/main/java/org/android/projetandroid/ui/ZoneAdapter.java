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
import org.android.projetandroid.ZoneLocationActivity;
import org.android.projetandroid.model.Zone;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZoneAdapter extends RecyclerView.Adapter<ZoneAdapter.ZoneViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private List<Zone> mZones;

    public ZoneAdapter(Activity context, List<Zone> zones){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mZones = zones;
    }

    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.zone_item, parent, false);
        ZoneAdapter.ZoneViewHolder holder = new ZoneAdapter.ZoneViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneViewHolder holder, int position) {
        final Zone zone = mZones.get(position);
        holder.mZoneCountryTextView.setText(zone.getCountry());
        holder.mZoneCityTextView.setText(zone.getCity());
        holder.mZoneNameTextView.setText(zone.getName());
        holder.mZoneLocationTextView.setText(Integer.toString(zone.getLocation()));
        holder.mZoneCountTextView.setText(Integer.toString(zone.getCount()));

        holder.mDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seeZoneLocationActivity = new Intent(context, ZoneLocationActivity.class);
                seeZoneLocationActivity.putExtra("zone", zone.getName());
                context.startActivity(seeZoneLocationActivity);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.mZones.size();
    }

    public void setZones(List<Zone> zones) {
        this.mZones = zones;
    }

    class ZoneViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.zone_adapter_country)
        TextView mZoneCountryTextView;

        @BindView(R.id.zone_adapter_city)
        TextView mZoneCityTextView;

        @BindView(R.id.zone_adapter_name)
        TextView mZoneNameTextView;

        @BindView(R.id.zone_adapter_location)
        TextView mZoneLocationTextView;

        @BindView(R.id.zone_adapter_count)
        TextView mZoneCountTextView;

        @BindView(R.id.zone_adapter_detail)
        Button mDetailButton;



        public ZoneViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
