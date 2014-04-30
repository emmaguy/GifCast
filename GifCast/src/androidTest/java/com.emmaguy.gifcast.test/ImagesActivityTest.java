package com.emmaguy.gifcast.test;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.Image;
import com.emmaguy.gifcast.ui.ImageDetailActivity;
import com.emmaguy.gifcast.ui.ImagesActivity;
import com.emmaguy.gifcast.ui.ImagesAdapter;
import com.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivityTest extends ActivityInstrumentationTestCase2<ImagesActivity> {
    private ImagesActivity mActivity;
    private Instrumentation mInstrumentation;

    private GridView mGridView;
    private ProgressBar mProgressBar;

    public ImagesActivityTest() {
        super(ImagesActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mInstrumentation = getInstrumentation();
        mActivity = getActivity();

        mGridView = (GridView) mActivity.findViewById(R.id.gridview);
        mProgressBar = (ProgressBar) mActivity.findViewById(R.id.progressbar);
    }

    @MediumTest
    public void test_once_progress_bar_is_hidden_gridview_has_some_items() {
        Solo solo = new Solo(mInstrumentation, mActivity);
        mInstrumentation.waitForIdleSync();

        solo.waitForCondition(new com.robotium.solo.Condition() {
            @Override
            public boolean isSatisfied() {
                return mProgressBar.getVisibility() == View.GONE;
            }
        }, 1000);

        assertTrue("Failed to load any images", mGridView.getAdapter().getCount() > 0);
    }

    @MediumTest
    public void test_tapping_an_item_opens_ImageDetailActivity() {
        Solo solo = new Solo(mInstrumentation, mActivity);
        mInstrumentation.waitForIdleSync();

        mInstrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ImagesAdapter adapter = new ImagesAdapter(mActivity, null, false);
                List images = new ArrayList<Image>();
                images.add(new Image("1", "Awesome Title", "gifs", false));
                adapter.addImages(images);

                mGridView.setAdapter(adapter);
            }
        });

        mInstrumentation.waitForIdleSync();
        assertTrue("Must have items to complete this test", mGridView.getAdapter().getCount() > 0);

        mInstrumentation.waitForIdleSync();
        solo.clickInList(1);

        mInstrumentation.waitForIdleSync();

        assertTrue("Failed to wait for ImageDetailActivity", solo.waitForActivity(ImageDetailActivity.class));

        mInstrumentation.waitForIdleSync();
        assertEquals("Incorrect title on ImageDetailActivity", "/r/gifs: Awesome Title", solo.getCurrentActivity().getActionBar().getTitle());
    }

    // test config change maintains position
}