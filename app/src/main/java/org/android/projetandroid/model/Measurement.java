package org.android.projetandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "Mesures")
public class Measurement extends Model {

    @Expose
    @Column(name="location", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String location;

    @Expose
    @Column(name="city")
    public String city;


    @Column(name="locationKey")
    public Location loc;



    // les mesures sont récupéré sous forme de liste de mesure
    @Expose
    public List<Values> measurements;

    // les mesures sont ensuite transformer en json
    // les mesures sont ensuite stocker sous forme de string
    @Column(name="mesure")
    public String mesure;

    public static class Values {

        @Expose
        public String parameter;

        @Expose
        public Float value;

        @Expose
        public String unit;

    }

}
