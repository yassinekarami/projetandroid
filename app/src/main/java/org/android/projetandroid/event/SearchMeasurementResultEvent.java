package org.android.projetandroid.event;

import org.android.projetandroid.model.Measurement;

import java.util.List;

public class SearchMeasurementResultEvent {

    private List<Measurement> measurements;

    public SearchMeasurementResultEvent(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    public List<Measurement> getMeasurements() {
        return this.measurements ;
    }
}
