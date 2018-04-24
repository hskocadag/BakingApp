package com.example.android.bakingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.model.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

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
