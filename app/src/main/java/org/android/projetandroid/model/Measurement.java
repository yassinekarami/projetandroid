package org.android.projetandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Mesures")
public class Measurement extends Model {
    @Expose
    @Column(name="location")
    public String location;

    @Column(name="valeurs")
    public Values mesurement;

    @Expose
    public String parameter;

    @Expose
    public double value;

    @Expose
    public String unit;

    public Measurement() {
        super();
    }

    public Measurement(String location, Values mesurement) {
        super();
        this.location = location;
        this.mesurement = mesurement;
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
        public Values(String parameter, String unit, double value) {
            super();
            this.parameter = parameter;
            this.unit = unit;
            this.value = value;
        }

    }

}
