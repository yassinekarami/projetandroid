package org.android.projetandroid.event;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class EventBusManager {

    public static Bus bus = new Bus(ThreadEnforcer.ANY);
}
