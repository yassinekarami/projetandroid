package org.android.projetandroid.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

@Table(name = "Location")
public class Location extends Model {

    @Expose
    @Column(name="locations")
    private List<String> locations;

    @Expose
    @Column(name="indicateur")
    private List<Indicateur> countsByMeasurement;

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

    public class Indicateur{

        @Expose
        String parameter;

        @Expose
        int count;

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
}
