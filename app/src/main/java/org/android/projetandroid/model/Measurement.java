package org.android.projetandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "mesures")
public class Measurement extends Model {
    @Expose
    @Column(name="location", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String location;

    @Column(name="measurement")
    public String mesurement;

    @Expose
    public String parameter;

    @Expose
    public double value;

    @Expose
    public String unit;

    public Measurement() {
        super();
    }

    public Measurement(String location, String mesurement) {
        super();
        this.location = location;
        this.mesurement = mesurement;
    }

    public static class Values {

        private String parameter;
        private String unit;
        private double value;

        public Values(String parameter, String unit, double value) {
            this.parameter = parameter;
            this.unit = unit;
            this.value = value;
        }

        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }

}
