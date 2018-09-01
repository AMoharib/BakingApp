package com.amoharib.bakingapp.activities;

import android.appwidget.AppWidgetManager;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amoharib.bakingapp.R;
import com.amoharib.bakingapp.fragments.DetailsFragment;
import com.amoharib.bakingapp.fragments.VideoFragment;
import com.amoharib.bakingapp.model.Result;
import com.amoharib.bakingapp.model.Step;
import com.amoharib.bakingapp.util.Constants;
import com.amoharib.bakingapp.widget.RecipeWidget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private boolean rotation;
    private String stepsJson, ingredientJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        stepsJson = getIntent().getStringExtra(Constants.KEY_STEPS);
        ingredientJson = getIntent().getStringExtra(Constants.KEY_INGREDIENTS);

        if (MainActivity.isTab) {
            Bundle bundle = new Bundle();
            List<Step> steps = new Gson().fromJson(stepsJson, new TypeToken<List<Step>>() {
            }.getType());
            bundle.putString(Constants.KEY_STEPS_URL, steps.get(0).getVideoURL());
            bundle.putString(Constants.KEY_STEPS_DESC, steps.get(0).getDescription());
            VideoFragment videoFragment = new VideoFragment();
            videoFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.video_fragment_container, videoFragment).commit();
        }

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_INGREDIENTS, ingredientJson);
            bundle.putString(Constants.KEY_STEPS, stepsJson);

            DetailsFragment detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.details_fragment, detailsFragment).commit();
        } else {
            rotation = savedInstanceState.getBoolean(Constants.KEY_ROTATION_DETAIL_ACTIVITY);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.getBoolean(Constants.KEY_ROTATION_DETAIL_ACTIVITY, rotation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add_to_widget:
                addToWidget();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToWidget() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        Result result = new Gson().fromJson(sharedPreferences.getString(Constants.WIDGET_RESULT, null), Result.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int appWidgetId = new Bundle().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        RecipeWidget.updateWidget(this, appWidgetManager, appWidgetId, result.getName(), result.getIngredients());
        Toast.makeText(this, result.getName() + " Added Successfully", Toast.LENGTH_SHORT).show();
    }
}
