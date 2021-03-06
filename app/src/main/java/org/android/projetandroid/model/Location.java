package org.android.projetandroid.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

@Table(name = "Locations")
public class Location extends Model implements Serializable {

    @Expose
    @Column(name="zone")
    public String city;

    @Expose
    @Column(name="location", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String location;

    @Expose
    @Column(name="coordinates")
    public Coordinates coordinates;

    @Column(name="favoris")
    public boolean favoris;


    public Location(){
        super();
    }

    @Table(name = "Coordinates")
    public static class Coordinates extends Model implements  Serializable{

        @Expose
        @Column(name="longitude")
        public double longitude = 0;

        @Expose
        @Column(name="latitude")
        public double latitude = 0;

    }

}
