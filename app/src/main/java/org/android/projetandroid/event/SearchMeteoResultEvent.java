package org.android.projetandroid.event;

import org.android.projetandroid.model.Meteo;

import java.util.List;

public class SearchMeteoResultEvent {

    private List<Meteo> meteos;

    public SearchMeteoResultEvent(List<Meteo> meteos) {
        this.meteos = meteos;
    }

    public List<Meteo> getMeteos() {
        return meteos;
    }

    public void setMeteos(List<Meteo> meteos) {
        this.meteos = meteos;
    }
}
