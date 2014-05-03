package com.emmaguy.gifcast.test;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.GridView;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.DrawableRequestQueue;
import com.emmaguy.gifcast.data.Image;
import com.emmaguy.gifcast.ui.ImageDetailActivity;
import com.emmaguy.gifcast.ui.ImagesActivity;
import com.emmaguy.gifcast.ui.ImagesAdapter;
import com.robotium.solo.Solo;

import java.util.Arrays;

public class ImagesActivityOpensDetailActivityWhenItemTappedTest extends ActivityInstrumentationTestCase2<ImagesActivity> {
    private Instrumentation mInstrumentation;
    private Solo mSolo;

    public ImagesActivityOpensDetailActivityWhenItemTappedTest() {
        super(ImagesActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mInstrumentation = getInstrumentation();
        mSolo = new Solo(mInstrumentation);

        Intent i = new Intent(getInstrumentation().getTargetContext(), ImagesActivity.class);
        i.putExtra("LOAD_IMAGES", false);
        setActivityIntent(i);

        given();
        when();
    }

    private void given() {
        ImagesActivity activity = getActivity();

        final GridView gridView = (GridView) activity.findViewById(R.id.gridview);

        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImagesAdapter adapter = buildAdapterWith1Image();
                    gridView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        assertEquals("No items in adapter", 1, gridView.getAdapter().getCount());
    }

    private ImagesAdapter buildAdapterWith1Image() {
        ImagesAdapter adapter = new ImagesAdapter(getActivity(), new MockRequestQueue(getActivity()), false);
        adapter.addImages(Arrays.asList(new Image("1", "Awesome Title", "gifs", false)));
        return adapter;
    }

    private void when() {
        mInstrumentation.waitForIdleSync();
        mSolo.clickInList(1); // indexed from 1
        mInstrumentation.waitForIdleSync();
    }

    @MediumTest
    public void test_tapping_an_item_opens_ImageDetailActivity_and_has_correct_title() {
        assertTrue("Failed to wait for ImageDetailActivity", mSolo.waitForActivity(ImageDetailActivity.class));

        mInstrumentation.waitForIdleSync();
        assertEquals("Incorrect title on ImageDetailActivity", "/r/gifs: Awesome Title", mSolo.getCurrentActivity().getActionBar().getTitle());
    }

    private class MockRequestQueue extends DrawableRequestQueue {
        public MockRequestQueue(Context c) {
            super(c);
        }
    }
}
