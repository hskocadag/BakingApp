package com.example.android.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class RecipeListActivityScreenTest {
    public static final String RECIPE_NAME = "Brownies";

    /**
     * The ActivityTestRule is a rule provided by Android used for functional testing of a single
     * activity. The activity that will be tested will be launched before each test that's annotated
     * with @Test and before methods annotated with @Before. The activity will be terminated after
     * the test and methods annotated with @After are complete. This rule allows you to directly
     * access the activity during the test.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Clicks on a GridView item and checks it opens up the OrderActivity with the correct details.
     */
    @Test
    public void clickGridViewItem_OpensRecipeDetailActivity() {

        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
        // gridview item and clicks it.
        onView(withId(R.id.rv_recipe_cards))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.rv_recipe_steps))
                .check(matches(hasDescendant(withText("Starting prep"))));

    }
}
