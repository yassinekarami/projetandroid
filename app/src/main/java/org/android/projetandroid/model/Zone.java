package org.android.projetandroid.model;

import com.google.gson.annotations.Expose;

public class Zone {

    @Expose
    private String country;

    @Expose
    private String name;

    @Expose
    private String city;

    @Expose
    private int count;

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

    @Expose
    public int location;

}
