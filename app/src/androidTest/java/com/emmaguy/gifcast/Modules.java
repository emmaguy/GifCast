package com.emmaguy.gifcast;

import com.emmaguy.gifcast.test.MockModule;

final class Modules {
    static Object[] list(GifCastApplication app) {
        return new Object[]{
                new MockModule(app)
        };
    }

    private Modules() {
    }
}
