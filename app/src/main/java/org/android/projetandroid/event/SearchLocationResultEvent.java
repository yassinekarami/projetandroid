package org.android.projetandroid.event;

import org.android.projetandroid.model.Location;

import java.util.List;

public class SearchLocationResultEvent {


    private List<Location> location;

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


