package com.example.android.bakingapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeStepAdapter;
import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.model.Step;
import com.google.android.exoplayer2.C;
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


public class RecipeStepVideoFragment extends Fragment implements ExoPlayer.EventListener{

    public static final String RECIPE_STEP_ID = "RECIPE_STEP_ID";
    private final String RECIPE_STEP_ID_STATE_KEY = "RECIPE_STEP_ID_STATE_KEY";
    private final String RECIPE_ID_STATE_KEY = "RECIPE_ID_STATE_KEY";
    private final String RECIPE_VIDEO_STATE_KEY = "RECIPE_VIDEO_STATE_KEY";
    private static final String TAG = RecipeStepVideoExplanationActivity.class.getSimpleName();

    private long mVideoPosition = C.TIME_UNSET;
    private int mRecipeStepId = -1;
    private int mRecipeId = -1;
    private TextView mRecipeLongDesc;
    private Button mNextButton;
    private Button mPrevButton;
    private RecipeStepAdapter.OnRecipeStepClickListener mOnRecipeStepClickListener;
    private boolean hasVideo;
    private LinearLayout mButtonPanel;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    public RecipeStepVideoFragment () {
    }

    public void setRecipe(int recipeStepId, int recipeId) {
        mRecipeId = recipeId;
        mRecipeStepId = recipeStepId;
    }

    public void setOnRecipeStepClickListener(RecipeStepAdapter.OnRecipeStepClickListener onRecipeStepClickListener){
        mOnRecipeStepClickListener = onRecipeStepClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_step_video, container, false);

        mPlayerView = rootView.findViewById(R.id.playerView);
        mRecipeLongDesc = rootView.findViewById(R.id.tv_recipe_step_long_desc);
        mNextButton = rootView.findViewById(R.id.next_button);
        mPrevButton = rootView.findViewById(R.id.prev_button);
        mButtonPanel = rootView.findViewById(R.id.button_panel);

        Bundle b = getArguments();
        if(b!= null && b.containsKey(RECIPE_STEP_ID)) {
            mRecipeStepId = b.getInt(RECIPE_STEP_ID, -1);
        }
        if (savedInstanceState != null) {
            mRecipeStepId = savedInstanceState.getInt(RECIPE_STEP_ID_STATE_KEY, -1);
            mRecipeId = savedInstanceState.getInt(RECIPE_ID_STATE_KEY, -1);
            mVideoPosition = savedInstanceState.getLong(RECIPE_VIDEO_STATE_KEY);
        }
        if(mRecipeStepId == -1)
            mRecipeStepId = 0;
        if(mRecipeId == -1 ) {
            Log.i(TAG, "No recipe selected");
            return rootView;
        }
        Step step = RecipeData.Recipes.get(mRecipeId).getSteps().get(mRecipeStepId);

        if (step.getVideoURL() == null || (step.getVideoURL().isEmpty() && step.getThumbnailURL().isEmpty())) {
            hasVideo = false;
            Toast.makeText(getContext(), "No video",
                    Toast.LENGTH_SHORT).show();
            mPlayerView.setVisibility(View.GONE);
        } else {
            initializeMediaSession();
            hasVideo = true;
            if(!step.getVideoURL().isEmpty())
                initializePlayer(Uri.parse(step.getVideoURL()));
            else
                initializePlayer(Uri.parse(step.getThumbnailURL()));
        }
        mRecipeLongDesc.setText(step.getDescription());
        // Initialize the player.

        updateScreenByConfiguration();
        setButtonFunctionalities();

        return rootView;
    }

    private void setButtonFunctionalities()
    {
        if(mRecipeStepId == -1 || mRecipeId == -1) {
            return;
        }
        int stepCount = RecipeData.Recipes.get(mRecipeId).getSteps().size();
        if(mRecipeStepId != 0) {
            mPrevButton.setVisibility(View.VISIBLE);
            mPrevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnRecipeStepClickListener != null)
                        mOnRecipeStepClickListener.onRecipeStepSelected(mRecipeStepId - 1);
                    else {
                        Intent intent = new Intent(getContext(), RecipeStepVideoExplanationActivity.class);
                        intent.putExtra(RecipeStepVideoFragment.RECIPE_STEP_ID, mRecipeStepId - 1);
                        intent.putExtra(RecipeStepListFragment.RECIPE_ID, mRecipeId);
                        getContext().startActivity(intent);
                    }
                }
            });
        } else {
            mPrevButton.setVisibility(View.INVISIBLE);
        }
        if(mRecipeStepId != stepCount -1) {
            mNextButton.setVisibility(View.VISIBLE);
            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnRecipeStepClickListener != null)
                        mOnRecipeStepClickListener.onRecipeStepSelected(mRecipeStepId + 1);
                    else {
                        Intent intent = new Intent(getContext(), RecipeStepVideoExplanationActivity.class);
                        intent.putExtra(RecipeStepVideoFragment.RECIPE_STEP_ID, mRecipeStepId + 1);
                        intent.putExtra(RecipeStepListFragment.RECIPE_ID, mRecipeId);
                        getContext().startActivity(intent);
                    }
                }
            });
        } else {
            mNextButton.setVisibility(View.INVISIBLE);
        }
    }

    private void updateScreenByConfiguration() {
        Configuration newConfig = getContext().getResources().getConfiguration();

        if (!RecipeDetailActivity.isTwoPane() && hasVideo && newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //First Hide other objects (listview or recyclerview), better hide them using Gone.
            showHideViews(false);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mPlayerView.getLayoutParams();
            params.width=params.MATCH_PARENT;
            params.height=params.MATCH_PARENT;
            mPlayerView.setLayoutParams(params);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //unhide your objects here.
            showHideViews(true);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mPlayerView.getLayoutParams();
            params.width=params.MATCH_PARENT;
            params.height=600;
            mPlayerView.setLayoutParams(params);
        }
    }

    private void showHideViews(boolean show)
    {
        if(show) {
            mRecipeLongDesc.setVisibility(View.VISIBLE);
            mButtonPanel.setVisibility(View.VISIBLE);
        } else {

            mRecipeLongDesc.setVisibility(View.INVISIBLE);
            mButtonPanel.setVisibility(View.INVISIBLE);
        }
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            if (mVideoPosition != C.TIME_UNSET)
                mExoPlayer.seekTo(mVideoPosition);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(RECIPE_ID_STATE_KEY, mRecipeId);
        outState.putInt(RECIPE_STEP_ID_STATE_KEY, mRecipeStepId);
        outState.putLong(RECIPE_VIDEO_STATE_KEY, mVideoPosition);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mRecipeStepId = savedInstanceState.getInt(RECIPE_STEP_ID_STATE_KEY, -1);
            mRecipeId = savedInstanceState.getInt(RECIPE_ID_STATE_KEY, -1);
            mVideoPosition = savedInstanceState.getLong(RECIPE_VIDEO_STATE_KEY);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mVideoPosition = mExoPlayer.getCurrentPosition();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void releasePlayer() {
        if(mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if(mMediaSession != null)
            mMediaSession.setActive(false);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}
