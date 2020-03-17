package org.android.projetandroid.service;

import com.activeandroid.query.Select;

import org.android.projetandroid.event.EventBusManager;
import org.android.projetandroid.event.SearchLocationFavorisResultEvent;
import org.android.projetandroid.model.Location;
import org.android.projetandroid.model.LocationFavoris;

import java.util.ArrayList;
import java.util.List;

public class LocationFavorisService {

    public static final LocationFavorisService INSTANCE = new LocationFavorisService();

    public LocationFavorisService() {}

    public void searchFavoriteLocation() {

        List<LocationFavoris> favoritelocation = new Select().from(LocationFavoris.class)
                .innerJoin(Location.Coordinates.class)
                .on("LocationFavoris.coordinates = Coordinates.Id")
                .execute();
        EventBusManager.bus.post(new SearchLocationFavorisResultEvent(favoritelocation));
    }
}

