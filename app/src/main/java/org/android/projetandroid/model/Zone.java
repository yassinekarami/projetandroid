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


}
