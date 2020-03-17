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

    @Column(name="identifiant")
    public long identifiant;

    @Column(name = "indicateur")
    public String indicateur;

    @Expose
    @Column(name="location")
    public String location;

    @Expose
    @Column(name="coordinates")
    public Coordinates coordinates;

    @Column(name="favoris")
    public boolean favoris;

    public Location(){
        super();
    }


    public List<Indicateur> getCountsByMeasurement() {
        return countsByMeasurement;
    }

    public void setCountsByMeasurement(List<Indicateur> countsByMeasurement) {
        this.countsByMeasurement = countsByMeasurement;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }


    public  class Indicateur extends Model implements Serializable{

        @Expose
        @Column(name="parameter")
        public String parameter;

        @Expose
        @Column(name="count")
        public int count;


        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class Coordinates extends Model implements  Serializable{

        @Expose
        @Column(name="longitude")
        public double longitude = 0;

        @Expose
        @Column(name="latitude")
        public double latitude = 0;


        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }



    }

}
