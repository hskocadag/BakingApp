package com.example.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.ui.RecipeStepListFragment;

import java.util.List;

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    private List<Recipe> mRecipes;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        mRecipes = RecipeData.Recipes;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mRecipes == null) return 0;
        return mRecipes.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (mRecipes == null || mRecipes.size() == 0 || mRecipes.size() <= position) return null;
        Recipe recipe = mRecipes.get(position);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_provider);

        views.setTextViewText(R.id.tv_widget_for_people, "For " + Integer.toString(recipe.getServings()));
        views.setTextViewText(R.id.tv_widget_recipe_name, recipe.getName());

        Bundle extras = new Bundle();
        extras.putInt(RecipeStepListFragment.RECIPE_ID, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.recipe_widget_layout, fillInIntent);

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