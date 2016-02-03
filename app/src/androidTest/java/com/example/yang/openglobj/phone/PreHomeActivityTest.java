package com.example.yang.openglobj.phone;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.example.yang.openglobj.R;

import org.junit.Test;

/**
 * Created by yang on 03/02/16.
 */
public class PreHomeActivityTest extends ActivityInstrumentationTestCase2<PreHomeActivity> {

    private PreHomeActivity mPreHomeActivity;
    private EditText mTestEdit;

    public PreHomeActivityTest() {
        super(PreHomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mPreHomeActivity = getActivity();
        mTestEdit =
                (EditText) mPreHomeActivity
                        .findViewById(R.id.editTextNote);
    }

    public void testPreconditions() {
        assertNotNull("mPreHomeActivity is null", mPreHomeActivity);
        assertNotNull("mTestEdit is null", mTestEdit);
    }

    @Test
    public void testOnCreate() throws Exception {

    }

    @Test
    public void testOnMyButtonClick() throws Exception {

    }

    @Test
    public void testOnListItemClick() throws Exception {

    }
}