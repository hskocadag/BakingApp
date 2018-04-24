package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.RecipeData;

public class RecipeStepVideoExplanationActivity extends AppCompatActivity{

    private int mRecipeStepId = -1;
    private int mRecipeId = -1;

    private final String RECIPE_STEP_ID_STATE_KEY = "RECIPE_STEP_ID_STATE_KEY";
    private final String RECIPE_ID_STATE_KEY = "RECIPE_ID_STATE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_video_explanation);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle b = intent.getExtras();
            mRecipeId = b.getInt(RecipeStepListFragment.RECIPE_ID, -1);
            mRecipeStepId = b.getInt(RecipeStepVideoFragment.RECIPE_STEP_ID, -1);
        }
        if(savedInstanceState != null)
        {
            mRecipeId = savedInstanceState.getInt(RECIPE_ID_STATE_KEY, -1);
            mRecipeStepId = savedInstanceState.getInt(RECIPE_STEP_ID_STATE_KEY, -1);
        }
        startFragment();
    }

    private void startFragment() {

        RecipeStepVideoFragment recipeStepVideoFragment = new RecipeStepVideoFragment();
        recipeStepVideoFragment.setRecipe(mRecipeStepId, mRecipeId);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.recipe_step_video_fragment, recipeStepVideoFragment)
                .commit();

        getSupportActionBar().setTitle(RecipeData.Recipes.get(mRecipeId).getSteps().get(mRecipeStepId).getShortDescription());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent!=null){
            Bundle b = intent.getExtras();
            mRecipeId = b.getInt(RecipeStepListFragment.RECIPE_ID, -1);
            mRecipeStepId = b.getInt(RecipeStepVideoFragment.RECIPE_STEP_ID, -1);
            startFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(RECIPE_ID_STATE_KEY, mRecipeId);
        outState.putInt(RECIPE_STEP_ID_STATE_KEY, mRecipeStepId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null)
        {
            mRecipeId = savedInstanceState.getInt(RECIPE_ID_STATE_KEY, -1);
            mRecipeStepId = savedInstanceState.getInt(RECIPE_STEP_ID_STATE_KEY, -1);
        }
    }
}
