package com.emmaguy.gifcast.test;

import com.emmaguy.gifcast.util.ImgurUrlParser;
import com.emmaguy.gifcast.RobolectricGradleTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
public class ImgurUrlParserTest {
    @Test
    public void test_parsing_valid_imgur_url_succeeds() {
        ImgurUrlParser parser = new ImgurUrlParser();

        assertTrue(parser.isImgurUrl("imgur.com/blah.gif"));
    }

    @Test
    public void test_parsing_invalid_imgur_url_fails() {
        ImgurUrlParser parser = new ImgurUrlParser();

        assertFalse(parser.isImgurUrl("twitter.com"));
    }

    @Test
    public void test_parsing_imgur_gallery_url_succeeds() {
        ImgurUrlParser parser = new ImgurUrlParser();

        assertTrue(parser.isImgurUrl("imgur.com/gallery"));
    }

    @Test
    public void test_parsing_imgur_album_url_succeeds() {
        ImgurUrlParser parser = new ImgurUrlParser();

        assertTrue(parser.isImgurUrl("imgur.com/a/"));
    }

    @Test
    public void test_parsing_non_imgur_gallery_url_fails() {
        ImgurUrlParser parser = new ImgurUrlParser();

        assertFalse(parser.isImgurUrl("twitter.com/gallery"));
    }

    @Test
    public void test_parsing_album_imgur_url_replaces_a_with_album() {
        ImgurUrlParser parser = new ImgurUrlParser();

        assertEquals("album/LRBlm", parser.parseUrl("http://imgur.com/a/LRBlm"));
    }

    @Test
    public void test_parsing_imgur_url_strips_off_imgur_dot_com() {
        ImgurUrlParser parser = new ImgurUrlParser();

        assertEquals("blah.gif", parser.parseUrl("imgur.com/blah.gif"));
    }

    @Test
    public void test_anything_after_hash_is_stripped_off() {
        ImgurUrlParser parser = new ImgurUrlParser();

        assertEquals("SCnIGUn", parser.parseUrl("http://i.imgur.com/SCnIGUn.jpg?5"));
    }
}