package com.amoharib.bakingapp.fragments;


import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amoharib.bakingapp.R;
import com.amoharib.bakingapp.util.Constants;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment implements ExoPlayer.EventListener {

    @BindView(R.id.player_view)
    SimpleExoPlayerView playerView;
    @BindView(R.id.description_tv)
    TextView descriptionTv;
    Unbinder unbinder;
    private String url, description;
    private long position;
    private boolean isReady;
    private MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder playbackBuilder;
    private SimpleExoPlayer player;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(Constants.KEY_STEPS_URL);
            description = bundle.getString(Constants.KEY_STEPS_DESC);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            isReady = savedInstanceState.getBoolean(Constants.KEY_PLAY_WHEN_READY);
            position = savedInstanceState.getLong(Constants.MEDIA_POS);

        }

        initializeMedia();
        initializePlayer(Uri.parse(url));
        checkOrientation();
        return view;
    }

    private void initializePlayer(Uri uri) {
        if (player == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance(
                    getActivity(), trackSelector, loadControl
            );
            playerView.setPlayer(player);
            player.addListener(this);

            String userAgent = Util.getUserAgent(getActivity(), getActivity().getString(R.string.application_name_exo_player));
            MediaSource mediaSource = new ExtractorMediaSource(
                    uri,
                    new DefaultDataSourceFactory(getActivity(), userAgent),
                    new DefaultExtractorsFactory()
                    , null,
                    null
            );
            player.prepare(mediaSource);
            player.setPlayWhenReady(true);
        }
    }

    private void initializeMedia() {
        mediaSessionCompat = new MediaSessionCompat(getActivity(), VideoFragment.class.getSimpleName());
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setMediaButtonReceiver(null);

        playbackBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSessionCompat.setPlaybackState(playbackBuilder.build());
        mediaSessionCompat.setCallback(new SessionCallBack());
        mediaSessionCompat.setActive(true);
    }

    private void checkOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            descriptionTv.setText(description);
        } else {
            hideUI();
            playerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            playerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    private void hideUI() {
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            position = player.getCurrentPosition();
            isReady = player.getPlayWhenReady();
            releasePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(isReady);
            player.seekTo(position);
        } else {
            initializeMedia();
            initializePlayer(Uri.parse(url));
            player.setPlayWhenReady(isReady);
            player.seekTo(position);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Constants.MEDIA_POS, position);
        outState.putBoolean(Constants.KEY_PLAY_WHEN_READY, isReady);

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
            playbackBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    player.getCurrentPosition(), 1f);
        } else if (playbackState == ExoPlayer.STATE_READY) {
            playbackBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    player.getCurrentPosition(), 1f);
        }
        mediaSessionCompat.setPlaybackState(playbackBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class SessionCallBack extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            super.onPlay();
            player.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            player.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            player.seekTo(0);
        }
    }
}
