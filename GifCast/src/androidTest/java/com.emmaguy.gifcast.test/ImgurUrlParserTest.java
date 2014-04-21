package com.emmaguy.gifcast.test;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.emmaguy.gifcast.ImgurUrlParser;

import junit.framework.Assert;

public class ImgurUrlParserTest extends InstrumentationTestCase {
    @SmallTest
    public void test_parsing_valid_imgur_url_succeeds() {
        ImgurUrlParser parser = new ImgurUrlParser();

        Assert.assertTrue(parser.isImgurUrl("imgur.com/blah.gif"));
    }

    @SmallTest
    public void test_parsing_invalid_imgur_url_fails() {
        ImgurUrlParser parser = new ImgurUrlParser();

        Assert.assertFalse(parser.isImgurUrl("twitter.com"));
    }

    @SmallTest
    public void test_parsing_imgur_gallery_url_succeeds() {
        ImgurUrlParser parser = new ImgurUrlParser();

        Assert.assertTrue(parser.isImgurUrl("imgur.com/gallery"));
    }

    @SmallTest
    public void test_parsing_non_imgur_gallery_url_fails() {
        ImgurUrlParser parser = new ImgurUrlParser();

        Assert.assertFalse(parser.isImgurUrl("twitter.com/gallery"));
    }

    @SmallTest
    public void test_parsing_imgur_url_strips_off_imgur_dot_com() {
        ImgurUrlParser parser = new ImgurUrlParser();

        Assert.assertEquals("blah.gif", parser.parseUrl("imgur.com/blah.gif"));
    }
}