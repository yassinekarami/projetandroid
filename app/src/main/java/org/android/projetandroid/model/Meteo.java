package org.android.projetandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Table(name = "Meteo")
public class Meteo extends Model implements Serializable {

    @Column(name="location", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String location;

    // les temperature contiennent les temperatures + la date
    @Column(name="tempCourante")
    public String courant;

    @Column(name="tempPrevision")
    public String prevision;


    public static class Temperature implements Serializable {

        public Object valeur;
        public String date;

        public Temperature() {}

    }







}
