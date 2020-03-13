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
    @Column(name="location")
    public List<String> locations;

    @Expose
    @Column(name="indicateur")
    public List<Indicateur> countsByMeasurement;

    @Expose
    @Column(name="coordinates")
    private Coordinates coordinates;

    public Coordinates getCoordinates(){
        return coordinates;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<Indicateur> getCountsByMeasurement() {
        return countsByMeasurement;
    }

    public void setCountsByMeasurement(List<Indicateur> countsByMeasurement) {
        this.countsByMeasurement = countsByMeasurement;
    }

    public class Indicateur implements Serializable{

        @Expose
        public String parameter;

        @Expose
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

    public class Coordinates implements  Serializable{
        @Expose
        @Column(name="longitude")
        double longitude = 0;

        @Expose
        @Column(name="latitude")
        double latitude = 0;

        public double getLongitude(){
            return this.longitude;
        }

        public void setLongitude(double longitude){
            this.longitude = longitude;
        }

        public double getLattitude(){
            return this.latitude;
        }

        public void setLattitude(double latitude){
            this.latitude = latitude;
        }
    }

}
