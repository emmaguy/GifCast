package com.emmaguy.gifcast.test;

import android.app.Instrumentation;
import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.GridView;

import com.emmaguy.gifcast.GifCastApplication;
import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.Image;
import com.emmaguy.gifcast.data.ImageLoader;
import com.emmaguy.gifcast.data.RequestQueue;
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

        mOriginalOrientation = getActivity().getResources().getConfiguration().orientation;

        givenActivityScrolledDown();
        whenConfigurationChanges();
    }

    private void givenActivityScrolledDown() {
        ImagesActivity activity = getActivity();
        GridView gridView = (GridView) activity.findViewById(R.id.gridview);

        assertEquals("No items in adapter", 20, gridView.getAdapter().getCount());

        mInstrumentation.waitForIdleSync();
        mSolo.scrollDownList((android.widget.AbsListView) getActivity().findViewById(R.id.gridview));
        mInstrumentation.waitForIdleSync();

        mItemAtTopOfGridViewBeforeConfigChange = mSolo.getImage(1).getTag().toString();

        assertNotSame("Old first item, should have scrolled", "0", mItemAtTopOfGridViewBeforeConfigChange);
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

        // TODO: this will break on tablets, perhaps check item is visible rather than index is identical?
        assertEquals("Item does not match: scroll position has not been correctly saved", indexAfterChange, indexBeforeChange);
    }
}