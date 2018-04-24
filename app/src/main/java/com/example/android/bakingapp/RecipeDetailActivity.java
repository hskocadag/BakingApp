package com.example.android.bakingapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeStepAdapter.OnRecipeStepClickListener {

    private int mRecipeId;
    private int mRecipeStepId = 0;

    private static boolean mTwoPane;

    private final String RECIPE_ID_STATE_KEY = "RECIPE_ID_STATE_KEY";
    private final String RECIPE_STEP_ID_STATE_KEY = "RECIPE_STEP_ID_STATE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Intent intent = getIntent();
        mRecipeId = -1;
        if(savedInstanceState != null) {
            mRecipeId = savedInstanceState.getInt(RECIPE_ID_STATE_KEY);
            mRecipeStepId = savedInstanceState.getInt(RECIPE_STEP_ID_STATE_KEY, 0);
        } else if(intent != null) {
            Bundle b = intent.getExtras();
            mRecipeId = b.getInt(RecipeStepListFragment.RECIPE_ID, -1);
        }
            FragmentManager fragmentManager = getSupportFragmentManager();
        RecipeStepListFragment recipeStepListFragment = new RecipeStepListFragment();
        recipeStepListFragment.setRecipeId(mRecipeId);
        fragmentManager.beginTransaction()
                .replace(R.id.master_list_fragment, recipeStepListFragment)
                .commit();

        if(findViewById(R.id.baking_app_linear_layout) != null) {
            mTwoPane = true;

            RecipeStepVideoFragment recipeStepVideoFragment = new RecipeStepVideoFragment();
            recipeStepVideoFragment.setRecipe(mRecipeStepId, mRecipeId);
            recipeStepVideoFragment.setOnRecipeStepClickListener(this);
            fragmentManager.beginTransaction()
                    .replace(R.id.video_explanation, recipeStepVideoFragment)
                    .commit();

        } else {
            mTwoPane = false;
        }
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