package com.emmaguy.gifcast.test;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.ui.ImagesActivity;
import com.robotium.solo.Solo;
// test config change maintains position
public class ImagesActivityHidesProgressBarWhenLoadingCompleteTest extends ActivityInstrumentationTestCase2<ImagesActivity> {
    private Instrumentation mInstrumentation;
    private Solo mSolo;

    private ImagesActivity mActivity;
    private ProgressBar mProgressBar;
    private GridView mGridView;

    public ImagesActivityHidesProgressBarWhenLoadingCompleteTest() {
        super(ImagesActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mInstrumentation = getInstrumentation();
        mSolo = new Solo(mInstrumentation);

        given();
        when();
    }

    void given() {
        mActivity = getActivity();
        mGridView = (GridView) mActivity.findViewById(R.id.gridview);
        mProgressBar = (ProgressBar) mActivity.findViewById(R.id.progressbar);
    }

    void when() {
        mInstrumentation.waitForIdleSync();

        mSolo.waitForCondition(new com.robotium.solo.Condition() {
            @Override
            public boolean isSatisfied() {
                return mProgressBar.getVisibility() == View.GONE;
            }
        }, 500);
    }

    @MediumTest
    public void test_once_progress_bar_is_hidden_gridview_has_some_items() {
        assertTrue("Failed to load any images", mGridView.getAdapter().getCount() > 0);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}