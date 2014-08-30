package com.emmaguy.gifcast.test;

import com.emmaguy.gifcast.RobolectricGradleTestRunner;
import com.emmaguy.gifcast.util.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
public class UtilsTest {

    @Test
    public void test_gif_is_recognised() {
        assertTrue(Utils.isGif("cats.gif"));
    }

    @Test
    public void test_gif_is_recognised_as_image() {
        assertTrue(Utils.isImage("cats.gif"));
    }
}
