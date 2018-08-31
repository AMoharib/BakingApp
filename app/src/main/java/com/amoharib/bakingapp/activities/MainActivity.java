package com.amoharib.bakingapp.activities;

import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amoharib.bakingapp.R;
import com.amoharib.bakingapp.util.SimpleIdlingResource;

public class MainActivity extends AppCompatActivity {

    private SimpleIdlingResource idlingResource;
    public static boolean isTab;

    @VisibleForTesting
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }
        return idlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getIdlingResource();
        isTab = findViewById(R.id.tab_recipe_fragment) != null;
    }
}
