package com.example.android.bakingapp;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.model.Ingredient;
import com.example.android.bakingapp.model.Step;

import java.util.List;

public class RecipeStepListFragment extends Fragment{

    public static final String RECIPE_ID = "RECIPE_ID";
    private int mRecipeId;

    private GridLayoutManager mGridLayoutManager;
    private final String RECIPE_STEP_LIST_STATE_KEY = "RECIPE_STEP_LIST_STATE_KEY";
    private Parcelable mListState;

    public RecipeStepListFragment () {
    }

    public void setRecipeId(int recipeId) {
        mRecipeId = recipeId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_master_recipe_step_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_recipe_steps);
        Bundle b = getArguments();
        if(b!= null)
            mRecipeId = b.getInt(RECIPE_ID, -1);
        // Create the adapter
        // This adapter takes in the context and an ArrayList of ALL the image resources to display
        mGridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mGridLayoutManager);
        RecipeStepAdapter mAdapter = new RecipeStepAdapter(getContext());
        if(mRecipeId != -1) {
            List<Step> steps = RecipeData.Recipes.get(mRecipeId).getSteps();
            if(steps.get(0).getShortDescription() != getResources().getString(R.string.ingredients_introduction)) {
                Step ingredients = new Step();
                ingredients.setShortDescription(getResources().getString(R.string.ingredients_introduction));
                ingredients.setDescription(ingredientListToString(RecipeData.Recipes.get(mRecipeId).getIngredients()));
                steps.add(0, ingredients);
            }
            mAdapter.updateRecipeStepsArray(steps, mRecipeId);
        }
        // Set the adapter on the GridView
        recyclerView.setAdapter(mAdapter);

        if (mListState != null) {
            mGridLayoutManager.onRestoreInstanceState(mListState);
        }
        // Return the root view
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mListState = mGridLayoutManager.onSaveInstanceState();
        outState.putParcelable(RECIPE_STEP_LIST_STATE_KEY, mListState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null)
            mListState = savedInstanceState.getParcelable(RECIPE_STEP_LIST_STATE_KEY);
    }

    private String ingredientListToString(List<Ingredient> ingredients) {
        StringBuilder sb = new StringBuilder();
        for(Ingredient ingredient : ingredients) {
            sb.append(ingredient.getQuantityStr())
                    .append(" ")
                    .append(ingredient.getMeasure())
                    .append(" ")
                    .append(ingredient.getIngredientName())
                    .append("\n");
        }
        return sb.toString();
    }
}
