package com.emmaguy.gifcast.test;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.emmaguy.gifcast.util.Utils;

import junit.framework.Assert;

public class UtilsTest extends InstrumentationTestCase {
    @SmallTest
    public void test_gif_is_recognised() {
        Assert.assertTrue(Utils.isGif("cats.gif"));
    }

    @SmallTest
    public void test_gif_is_recognised_as_image() {
        Assert.assertTrue(Utils.isImage("cats.gif"));
    }
}
