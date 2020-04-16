package org.android.projetandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Mesures")
public class Measurement extends Model {

    @Expose
    public String location;

    @Expose
    public String parameter;

    @Expose
    public double value;

    @Expose
    public String unit;

    @Column(name="valeurs")
    public Values mesurement;

    @Column(name="location")
    public Location city;



    public Measurement() {
        super();
    }


    @Table(name="Valeurs")
    public static class Values extends Model {

        @Column(name = "parametre")
        public String parameter;

        @Column(name="unit")
        public String unit;

        @Column(name="value")
        public double value;

        public Values() {
            super();
        }

    }

}
