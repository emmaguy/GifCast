package com.emmaguy.gifcast.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.GridView;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.Image;
import com.emmaguy.gifcast.ui.ImageDetailActivity;
import com.emmaguy.gifcast.ui.ImagesActivity;
import com.emmaguy.gifcast.ui.ImagesAdapter;
import com.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivityOpensDetailActivityWhenTappedUnit extends ActivityUnitTestCase<ImagesActivity> {
    private Instrumentation mInstrumentation;
    private Solo mSolo;

    private ImagesActivity mActivity;
    private GridView mGridView;

    public ImagesActivityOpensDetailActivityWhenTappedUnit() {
        super(ImagesActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mInstrumentation = getInstrumentation();
        mSolo = new Solo(mInstrumentation);

        Intent intent = new Intent(getInstrumentation().getTargetContext(), ImagesActivity.class);
        startActivity(intent, null, null);
        mActivity = getActivity();

        getInstrumentation().callActivityOnStart(mActivity);
        getInstrumentation().callActivityOnResume(mActivity);

        given();
        when();
    }

    private ImagesAdapter buildAdapterWith1Image() {
        ImagesAdapter adapter = new ImagesAdapter(getInstrumentation().getContext(), null, false);

        List images = new ArrayList<Image>();
        images.add(new Image("1", "Awesome Title", "gifs", false));
        adapter.addImages(images);

        return adapter;
    }

    void given() {
        mGridView = (GridView) mActivity.findViewById(R.id.gridview);

        ImagesAdapter adapter = buildAdapterWith1Image();
        mGridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        assertEquals(1, adapter.getCount());

        View firstItem = mGridView.getAdapter().getView(0, null, null);
        assertNotNull(firstItem);

        mGridView.performItemClick(firstItem, 0, mGridView.getAdapter().getItemId(0));
    }

    void when() {
        mSolo.waitForView(R.id.gridview);
        mSolo.clickInList(0, 0);
        mInstrumentation.waitForIdleSync();
    }

    @MediumTest
    public void test_tapping_an_item_opens_ImageDetailActivity_and_has_correct_title() {
        assertTrue("Failed to wait for ImageDetailActivity", mSolo.waitForActivity(ImageDetailActivity.class));

        mInstrumentation.waitForIdleSync();
        assertEquals("Incorrect title on ImageDetailActivity", "/r/gifs: Awesome Title", mActivity.getActionBar().getTitle());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
