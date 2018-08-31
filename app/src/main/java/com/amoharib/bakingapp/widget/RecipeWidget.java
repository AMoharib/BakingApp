package com.amoharib.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.amoharib.bakingapp.R;
import com.amoharib.bakingapp.model.Ingredient;
import com.amoharib.bakingapp.model.Result;
import com.amoharib.bakingapp.util.Constants;
import com.amoharib.bakingapp.util.StringUtils;
import com.google.gson.Gson;

import java.util.List;

public class RecipeWidget extends AppWidgetProvider {

    private SharedPreferences sharedPreferences;

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, String recipeName, List<Ingredient> ingredients) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        remoteViews.setTextViewText(R.id.widget_title, recipeName);
        remoteViews.removeAllViews(R.id.container);

        RemoteViews child = new RemoteViews(context.getPackageName(), R.layout.ingredients_textview);
        child.setTextViewText(R.id.ingredient_tv, StringUtils.concatString(ingredients));
        remoteViews.addView(R.id.container, child);

        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String resultJson = sharedPreferences.getString(Constants.WIDGET_RESULT, null);

        Result result = new Gson().fromJson(resultJson, Result.class);
        String recipeName = result.getName();
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, id, recipeName, result.getIngredients());
        }
    }
}
