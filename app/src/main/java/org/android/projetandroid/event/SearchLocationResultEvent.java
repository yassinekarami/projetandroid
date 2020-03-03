package org.android.projetandroid.event;

import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.Zone;

import java.util.List;

public class SearchLocationResultEvent {


    public Zone zone = new Zone();
    private List<Location> location;

    public SearchLocationResultEvent(Zone zone, List<Location> location) {
        this.zone = zone;
        this.location = location;
    }

    public SearchLocationResultEvent(List<Location> location) {
        this.location = location;
    }
    
    public List<Location> getLocation() {
        return location;
    }

    public void setLocation(List<Location> location) {
        this.location = location;
    }




}


