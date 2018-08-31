package com.amoharib.bakingapp.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.amoharib.bakingapp.R;
import com.amoharib.bakingapp.fragments.VideoFragment;
import com.amoharib.bakingapp.model.Step;
import com.amoharib.bakingapp.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity {

    @BindView(R.id.next_step_btn)
    FloatingActionButton nextStepBtn;
    @BindView(R.id.prev_step_btn)
    FloatingActionButton prevStepBtn;
    private boolean fragmentCreated;
    private List<Step> steps;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            fragmentCreated = savedInstanceState.getBoolean(Constants.KEY_ROTATION_VIDEO_ACTIVITY);
        }

        steps = new Gson().fromJson(getIntent().getStringExtra(Constants.KEY_STEPS_JSON),
                new TypeToken<List<Step>>() {
                }.getType());
        position = getIntent().getIntExtra(Constants.KEY_STEP_POSITION, 0);
        showFragment(position);
        prevStepBtn.setEnabled(false);

        nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position + 1 < steps.size()) {
                    fragmentCreated = false;
                    showFragment(++position);
                    prevStepBtn.setEnabled(true);
                }
                if (position + 1 == steps.size()) {
                    nextStepBtn.setEnabled(false);
                }
            }
        });

        prevStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position - 1 >= 0) {
                    fragmentCreated = false;
                    showFragment(--position);
                    nextStepBtn.setEnabled(true);
                } else {
                    prevStepBtn.setEnabled(false);
                }
            }
        });
    }

    private void showFragment(int position) {
        Step step = steps.get(position);
        if (!fragmentCreated) {
            fragmentCreated = true;
            VideoFragment videoFragment = new VideoFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_STEPS_URL, step.getVideoURL());
            bundle.putString(Constants.KEY_STEPS_DESC, step.getDescription());
            videoFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.video_fragment, videoFragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.KEY_ROTATION_VIDEO_ACTIVITY, fragmentCreated);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
