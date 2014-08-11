package com.emmaguy.gifcast.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.GridView;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.GifCastApplication;
import com.emmaguy.gifcast.data.Image;
import com.emmaguy.gifcast.ui.ImagesActivity;
import com.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivityRetainsScrollPositionWhenOrientationChangesTest extends ActivityInstrumentationTestCase2<ImagesActivity> {
    private Instrumentation mInstrumentation;
    private Solo mSolo;

    private String mItemAtTopOfGridViewBeforeConfigChange;
    private int mOriginalOrientation;

    public ImagesActivityRetainsScrollPositionWhenOrientationChangesTest() {
        super(ImagesActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mInstrumentation = getInstrumentation();
        mSolo = new Solo(mInstrumentation);

        // intent to not enable endless scrolling
        Intent i = new Intent(getInstrumentation().getTargetContext(), ImagesActivity.class);
        i.putExtra("ENABLE_ENDLESS", false);
        setActivityIntent(i);

        GifCastApplication app = (GifCastApplication) getInstrumentation().getTargetContext().getApplicationContext();
        app.setImages(createImages());

        mOriginalOrientation = getActivity().getResources().getConfiguration().orientation;

        givenActivityScrolledDown();
        whenConfigurationChanges();
    }

    private void givenActivityScrolledDown() {
        ImagesActivity activity = getActivity();
        GridView gridView = (GridView) activity.findViewById(R.id.gridview);

        assertEquals("No items in adapter", 100, gridView.getAdapter().getCount());

        mInstrumentation.waitForIdleSync();
        mSolo.scrollDownList((android.widget.AbsListView) getActivity().findViewById(R.id.gridview));
        mInstrumentation.waitForIdleSync();

        mItemAtTopOfGridViewBeforeConfigChange = mSolo.getImage(1).getTag().toString();

        assertNotSame("Old first item, should have scrolled", "0", mItemAtTopOfGridViewBeforeConfigChange);
    }

    private List<Image> createImages() {
        List<Image> images = new ArrayList<Image>();

        for(int i = 0; i < 100; i++) {
            Image image = new Image("" + i, "Awesome Item: " + i, "gifs", false);
            image.updateUrl("" + i);
            images.add(image);
        }

        return images;
    }

    private void whenConfigurationChanges() {
        mInstrumentation.waitForIdleSync();
        mSolo.setActivityOrientation(mOriginalOrientation == Configuration.ORIENTATION_PORTRAIT ? Configuration.ORIENTATION_LANDSCAPE : Configuration.ORIENTATION_LANDSCAPE);
        mInstrumentation.waitForIdleSync();
    }

    @MediumTest
    public void test_stillInCorrectPosition() {
        String text = mSolo.getImage(1).getTag().toString();

        assertNotSame("First item, should have scrolled", "0", text);

        int indexBeforeChange = Integer.parseInt(mItemAtTopOfGridViewBeforeConfigChange);
        int indexAfterChange = Integer.parseInt(text);

        // should be in range, +3 or -3 as item may not still be very first one (i.e. top left), particularly on tablets
        if(indexBeforeChange < indexAfterChange) {
            assertTrue(indexBeforeChange + 1 == indexAfterChange || indexBeforeChange + 2 == indexAfterChange || indexBeforeChange + 3 == indexAfterChange);
        } else if (indexBeforeChange > indexAfterChange) {
            assertTrue(indexBeforeChange - 1 == indexAfterChange || indexBeforeChange - 2 == indexAfterChange || indexBeforeChange - 3 == indexAfterChange);
        } else {
            assertEquals("Item does not match: scroll position has not been correctly saved", mItemAtTopOfGridViewBeforeConfigChange, text);
        }
    }
}