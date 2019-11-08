package com.example.missyou;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

// test for Button(Location) Click on NewPost
public class NewPostActivityTest {

    @Rule
    public ActivityTestRule<NewPostActivity> newPostActivityTestRule = new ActivityTestRule< NewPostActivity>(NewPostActivity.class);
    private NewPostActivity newPostActivity= null;
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(PetLocationMapsActivity.class.getName(),null,false);

    @Before

    public void setUp() throws Exception {

        newPostActivity = newPostActivityTestRule.getActivity();
    }
    @Test
    public void NewPostButtonClickTest(){

        assertNotNull(newPostActivity.findViewById(R.id.btnMap));
        onView(withId(R.id.btnMap)).perform(click());
        Activity PetLocationMapsActivity = getInstrumentation().waitForMonitorWithTimeout(monitor,5000); //ms
        assertNotNull(PetLocationMapsActivity);
        PetLocationMapsActivity.finish();

    }

    @After
    public void tearDown() throws Exception {
        newPostActivity = null;
    }
}