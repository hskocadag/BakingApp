package com.example.android.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RemoteViews;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeStepAdapter;
import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.widget.RecipeWidgetProvider;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeStepAdapter.OnRecipeStepClickListener {

    private int mRecipeId;
    private int mRecipeStepId = 0;

    private static boolean mTwoPane;
    private static boolean mStateStored = false;

    private final String RECIPE_ID_STATE_KEY = "RECIPE_ID_STATE_KEY";
    private final String RECIPE_STEP_ID_STATE_KEY = "RECIPE_STEP_ID_STATE_KEY";

    private MenuItem selectRecipeMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Intent intent = getIntent();
        mRecipeId = -1;
        if(savedInstanceState != null) {
            mStateStored = true;
            mRecipeId = savedInstanceState.getInt(RECIPE_ID_STATE_KEY);
            mRecipeStepId = savedInstanceState.getInt(RECIPE_STEP_ID_STATE_KEY, 0);
        } else if(intent != null && intent.getExtras() != null) {
            Bundle b = intent.getExtras();
            mRecipeId = b.getInt(RecipeStepListFragment.RECIPE_ID, -1);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(!mStateStored) {
            RecipeStepListFragment recipeStepListFragment = new RecipeStepListFragment();
            recipeStepListFragment.setRecipeId(mRecipeId);
            fragmentManager.beginTransaction()
                    .replace(R.id.master_list_fragment, recipeStepListFragment)
                    .commit();
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(RecipeData.Recipes.get(mRecipeId).getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if(getActionBar() != null) {
            getActionBar().setTitle(RecipeData.Recipes.get(mRecipeId).getName());
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(!mStateStored && findViewById(R.id.baking_app_linear_layout) != null) {
            mTwoPane = true;

            RecipeStepVideoFragment recipeStepVideoFragment = new RecipeStepVideoFragment();
            recipeStepVideoFragment.setRecipe(mRecipeStepId, mRecipeId);
            recipeStepVideoFragment.setOnRecipeStepClickListener(this);
            fragmentManager.beginTransaction()
                    .replace(R.id.video_explanation, recipeStepVideoFragment)
                    .commit();
        } else if(mStateStored && findViewById(R.id.baking_app_linear_layout) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
        if(mStateStored)
            mStateStored = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_actvity_menu, menu);
        selectRecipeMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.show_on_widget_menu_item)
        {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
            RecipeData.widgetRecipeId = mRecipeId;
            //Trigger data update to handle the GridView widgets and force a data refresh
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);

        } else if(id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putInt(RECIPE_ID_STATE_KEY, mRecipeId);
        state.putInt(RECIPE_STEP_ID_STATE_KEY, mRecipeStepId);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        if (state != null)
            mRecipeId = state.getInt(RECIPE_ID_STATE_KEY);
    }

    public static boolean isTwoPane() {
        return mTwoPane;
    }

    @Override
    public void onRecipeStepSelected(int position) {
        mRecipeStepId = position;
        //Toast.makeText(this, "Position clicked = " + position, Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Handle the two-pane case and replace existing fragments right when a new image is selected from the master list
        if (mTwoPane) {

            RecipeStepVideoFragment recipeStepVideoFragment = new RecipeStepVideoFragment();
            recipeStepVideoFragment.setRecipe(position, mRecipeId);
            recipeStepVideoFragment.setOnRecipeStepClickListener(this);
            fragmentManager.beginTransaction()
                    .replace(R.id.video_explanation, recipeStepVideoFragment)
                    .commit();

        } else {

            Intent intent = new Intent(this, RecipeStepVideoExplanationActivity.class);
            intent.putExtra(RecipeStepVideoFragment.RECIPE_STEP_ID, position);
            intent.putExtra(RecipeStepListFragment.RECIPE_ID, mRecipeId);
            this.startActivity(intent);
        }
    }
}
