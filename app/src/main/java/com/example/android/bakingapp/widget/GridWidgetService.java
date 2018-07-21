package com.example.android.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.model.Ingredient;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.ui.RecipeDetailActivity;
import com.example.android.bakingapp.ui.RecipeStepListFragment;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.List;

public class GridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Bundle bundle = intent.getExtras();
        int recipeId = -1;
        if(bundle != null) {
                recipeId = bundle.getInt(RecipeStepListFragment.RECIPE_ID, -1);
                recipeId = recipeId == -1 ? RecipeData.widgetRecipeId : recipeId;
        }
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }

}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    private int mRecipeId;
    private List<Ingredient> mIngredients;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
        mRecipeId = RecipeData.widgetRecipeId;
    }

    @Override
    public void onCreate() {
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        mRecipeId = RecipeData.widgetRecipeId;
        if(RecipeData.Recipes != null && mRecipeId >= 0 && mRecipeId < RecipeData.Recipes.size()) {
            mIngredients = RecipeData.Recipes.get(mRecipeId).getIngredients();
        } else
            mIngredients = null;

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mRecipeId == -1 || mIngredients == null) return 0;
        return mIngredients.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (mIngredients == null || mIngredients.size() == 0 || position >= mIngredients.size()) return null;

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_provider);
        Ingredient ingredient = mIngredients.get(position);
        String ingredientExp = ingredient.getQuantityStr() + " " + ingredient.getMeasure() + " " + ingredient.getIngredientName();
        views.setTextViewText(R.id.tv_widget_recipe_name, ingredientExp);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}