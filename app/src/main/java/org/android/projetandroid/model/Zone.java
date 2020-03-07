package org.android.projetandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Zones")
public class Zone extends Model {

    @Expose
    @Column(name = "country")
    public String country;

    @Expose
    @Column(name = "name", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String name;

    @Expose
    @Column(name = "city")
    public String city;

    @Expose
    @Column(name = "count")
    public int count;

    @Expose
    @Column(name = "nblocation")
    public int locations;

    public Zone() {
        super();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public int getLocations() {
        return locations;
    }

    public void setLocations(int locations) {
        this.locations = locations;
    }






}
