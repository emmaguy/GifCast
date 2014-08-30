package com.emmaguy.gifcast.test;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.GridView;
import android.widget.ImageView;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.Image;
import com.emmaguy.gifcast.ui.ImageDetailActivity;
import com.emmaguy.gifcast.ui.ImagesActivity;
import com.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.List;

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

        givenActivityScrolledDown();
        whenDetailActivityOpened();
    }

    private void givenActivityScrolledDown() {
        ImagesActivity activity = getActivity();
        GridView gridView = (GridView) activity.findViewById(R.id.gridview);

        assertEquals("No items in adapter", 100, gridView.getAdapter().getCount());

        mInstrumentation.waitForIdleSync();
        mSolo.scrollDownList((android.widget.AbsListView) getActivity().findViewById(R.id.gridview));
        mInstrumentation.waitForIdleSync();
    }

    private List<Image> createImages() {
        List<Image> images = new ArrayList<Image>();

        for (int i = 0; i < 100; i++) {
            Image image = new Image("" + i, "Awesome Item: " + i, "gifs", false);
            image.updateUrl("" + i);
            images.add(image);
        }

        return images;
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
