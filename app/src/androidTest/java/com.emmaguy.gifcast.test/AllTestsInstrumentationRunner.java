package com.emmaguy.gifcast.test;

import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;

import junit.framework.TestSuite;

public class AllTestsInstrumentationRunner extends InstrumentationTestRunner {

    @Override
    public TestSuite getAllTests() {
        InstrumentationTestSuite suite = new InstrumentationTestSuite(this);
        suite.addTestSuite(com.emmaguy.gifcast.test.UtilsTest.class);
        suite.addTestSuite(com.emmaguy.gifcast.test.ImgurUrlParserTest.class);
        suite.addTestSuite(com.emmaguy.gifcast.test.ImagesActivityRetainsScrollPositionWhenOrientationChangesTest.class);
        suite.addTestSuite(com.emmaguy.gifcast.test.ImagesActivityRetainsScrollPositionWhenDetailActivityClosedTest.class);
        return suite;
    }

    @Override
    public ClassLoader getLoader() {
        return InstrumentationTestRunner.class.getClassLoader();
    }
}