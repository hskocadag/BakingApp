package com.example.android.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.android.bakingapp.IdlingResource.RecipeDownloadIdlingResource;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeAdapter;
import com.example.android.bakingapp.RecipeWidgetProvider;
import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.GridItemClickListener {

    private static int GRID_LAYOUT_SPAN_COUNT = 1;
    private TextView mErrorMessage;
    private RecyclerView mRecipesRecyclerView;
    private RecipeAdapter mRecipeAdapter;
    private List<Recipe> mRecipes = new ArrayList<Recipe>();
    private GridLayoutManager mGridLayoutManager;
    private final String LIST_STATE_KEY = "LIST_STATE_KEY";
    private Parcelable mListState;
    @Nullable
    private RecipeDownloadIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessage = findViewById(R.id.tv_error_message);
        mRecipesRecyclerView = findViewById(R.id.rv_recipe_cards);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            GRID_LAYOUT_SPAN_COUNT = 3;
        else
            GRID_LAYOUT_SPAN_COUNT = 1;

        mGridLayoutManager = new GridLayoutManager(this, GRID_LAYOUT_SPAN_COUNT, GridLayoutManager.VERTICAL, false);
        mErrorMessage.setVisibility(View.GONE);
        mRecipesRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecipeAdapter = new RecipeAdapter(this, this);
        mRecipesRecyclerView.setAdapter(mRecipeAdapter);

        loadRecipes();
    }

    private void loadRecipes()
    {
        if(mIdlingResource != null)
            mIdlingResource.setIdleState(false);
        new DownloadRecipesTask().execute();
    }

    @Override
    public void onGridItemClick(int clickedItemId) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeStepListFragment.RECIPE_ID, clickedItemId);
        startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GRID_LAYOUT_SPAN_COUNT = 3;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            GRID_LAYOUT_SPAN_COUNT = 1;
        }
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        mListState = mGridLayoutManager.onSaveInstanceState();
        state.putParcelable(LIST_STATE_KEY, mListState);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        if (state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }

    public class DownloadRecipesTask extends AsyncTask<URL, Integer, List<Recipe>> {
        protected List<Recipe> doInBackground(URL... urls) {
            try {
                String recipesJSON = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl());
                Gson g = new Gson();
                List<Recipe> recipes = g.fromJson(recipesJSON, new TypeToken<List<Recipe>>(){}.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mListState != null) {
                            mGridLayoutManager.onRestoreInstanceState(mListState);
                        }
                    }});
                return recipes;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            return;
        }

        protected void onPostExecute(List<Recipe> result) {
            if(result == null) {
                mErrorMessage.setVisibility(View.VISIBLE);
                mRecipesRecyclerView.setVisibility(View.GONE);
                return;
            }
            mRecipes = result;
            RecipeData.Recipes = result;
            mRecipeAdapter.updateRecipesArray(mRecipes);
            if(mIdlingResource != null)
                mIdlingResource.setIdleState(true);
            mErrorMessage.setVisibility(View.GONE);
            mRecipesRecyclerView.setVisibility(View.VISIBLE);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(MainActivity.this, RecipeWidgetProvider.class));
            //Trigger data update to handle the GridView widgets and force a data refresh
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
        }
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new RecipeDownloadIdlingResource();
        }
        return mIdlingResource;
    }
}
