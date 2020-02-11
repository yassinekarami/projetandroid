package org.android.projetandroid.event;

import org.android.projetandroid.model.Zone;

import java.util.List;

public class SearchResultEvent {

    private List<Zone> zone;

    public SearchResultEvent(List<Zone> zone) {
        this.zone = zone;
    }

    public List<Zone> getZones() {
        return this.zone ;
    }
}
