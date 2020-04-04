package org.android.projetandroid.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table(name = "Locations")
public class Location extends Model implements Serializable {

    @Expose
    public List<Indicateur> countsByMeasurement = new ArrayList<>();

    @Expose
    @Column(name="zone")
    public String city;

    @Column(name = "indicateur")
    public String indicateur;

    @Expose
    @Column(name="location", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String location;

    @Expose
    @Column(name="coordinates")
    public Coordinates coordinates;

    @Column(name="favoris")
    public boolean favoris;


    public Location(){
        super();
    }


    public  class Indicateur extends Model implements Serializable{

        @Expose
        @Column(name="parameter")
        public String parameter;

        @Expose
        @Column(name="count")
        public int count;

    }

    public static class Coordinates extends Model implements  Serializable{

        @Expose
        @Column(name="longitude")
        public double longitude = 0;

        @Expose
        @Column(name="latitude")
        public double latitude = 0;


    }

}
