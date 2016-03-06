package me.sxwang.afinder;

import com.squareup.otto.Bus;

/**
 * Created by wang on 3/6/16.
 */
public final class BusProvider {
    private static final Bus UI_BUS = new Bus();

    public static Bus getUIBus() {
        return UI_BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
