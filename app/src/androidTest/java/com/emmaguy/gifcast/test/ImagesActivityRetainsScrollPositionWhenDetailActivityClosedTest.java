package com.emmaguy.gifcast.test;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.GridView;
import android.widget.ImageView;

import com.emmaguy.gifcast.GifCastApplication;
import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.ImageLoader;
import com.emmaguy.gifcast.data.RequestQueue;
import com.emmaguy.gifcast.ui.ImageDetailActivity;
import com.emmaguy.gifcast.ui.ImagesActivity;
import com.robotium.solo.Solo;

public class ImagesActivityRetainsScrollPositionWhenDetailActivityClosedTest extends ActivityInstrumentationTestCase2<ImagesActivity> {
    private Instrumentation mInstrumentation;
    private Solo mSolo;

    private String mItemAtTopOfGridView;

    public ImagesActivityRetainsScrollPositionWhenDetailActivityClosedTest() {
        super(ImagesActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mInstrumentation = getInstrumentation();
        mSolo = new Solo(mInstrumentation);

        injectDependencies();

        givenActivityScrolledDown();
        whenDetailActivityOpened();
    }

    private void injectDependencies() {
        GifCastApplication app = (GifCastApplication) getInstrumentation().getTargetContext().getApplicationContext();

        assertNotNull("GifCastApplication is null", app);

        mInstrumentation.waitForIdleSync();

        RequestQueue r = app.requestQueue();
        ImageLoader l = app.imageLoader();

        assertNotNull("Request queue null, has not been injected", r);
        assertNotNull("Image loader null, has not been injected", l);

        assertEquals(com.emmaguy.gifcast.test.MockRequestQueue.class, r.getClass());
        assertEquals(com.emmaguy.gifcast.test.MockImageLoader.class, l.getClass());

        assertNotSame("Image loader has no items", 0, l.getAllImages().size());
    }

    private void givenActivityScrolledDown() {
        GridView gridView = (GridView) getActivity().findViewById(R.id.gridview);

        assertEquals("No items in adapter", 20, gridView.getAdapter().getCount());

        mInstrumentation.waitForIdleSync();
        mSolo.scrollDownList((android.widget.AbsListView) getActivity().findViewById(R.id.gridview));
        mInstrumentation.waitForIdleSync();
    }

    private void whenDetailActivityOpened() {
        ImageView imageViewAtTop = mSolo.getImage(1);

        mItemAtTopOfGridView = imageViewAtTop.getTag().toString();
        assertNotSame("Old first item; should have scrolled", "0", mItemAtTopOfGridView);

        mInstrumentation.waitForIdleSync();
        mSolo.clickOnView(imageViewAtTop);
        mInstrumentation.waitForIdleSync();
    }

    @MediumTest
    public void test_tapping_an_item_opens_ImageDetailActivity_and_has_correct_title() {
        assertTrue("Failed to wait for ImageDetailActivity", mSolo.waitForActivity(ImageDetailActivity.class));

        mInstrumentation.waitForIdleSync();
        String title = mSolo.getCurrentActivity().getActionBar().getTitle().toString();
        assertTrue("Incorrect title on ImageDetailActivity " + title, title.startsWith("/r/gifs: Awesome Item"));
    }

    @MediumTest
    public void test_pressing_back_maintains_scroll_position() {
        mSolo.clickOnActionBarHomeButton();

        mSolo.waitForActivity(ImagesActivity.class);

        assertEquals("Lost scroll position on back", mItemAtTopOfGridView, mSolo.getImage(1).getTag());
    }
}
