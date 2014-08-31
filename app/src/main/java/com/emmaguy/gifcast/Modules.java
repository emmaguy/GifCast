package com.emmaguy.gifcast;

import com.emmaguy.gifcast.modules.AndroidModule;

// in androidTest dir this is overridden by MockModule
final class Modules {
    static Object[] list(GifCastApplication app) {
        return new Object[]{
                new AndroidModule(app)
        };
    }

    private Modules() {
    }
}
