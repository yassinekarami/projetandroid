package org.android.projetandroid.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Zones")
public class Zone extends Model {


    @Expose
    @Column(name = "country")
    private String country;

    @Expose
    @Column(name = "name")
    private String name;

    @Expose
    @Column(name = "city")
    private String city;

    @Expose
    @Column(name = "count")
    private int count;

    @Expose
    @Column(name = "location")
    private int location;


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

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }


}
