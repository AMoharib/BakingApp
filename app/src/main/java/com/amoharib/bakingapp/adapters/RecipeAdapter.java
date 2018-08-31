package com.amoharib.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amoharib.bakingapp.R;
import com.amoharib.bakingapp.activities.DetailsActivity;
import com.amoharib.bakingapp.model.Ingredient;
import com.amoharib.bakingapp.model.Result;
import com.amoharib.bakingapp.model.Step;
import com.amoharib.bakingapp.util.Constants;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {
    private List<Result> results;
    private Context context;
    private SharedPreferences sharedPreferences;

    public RecipeAdapter(List<Result> results, Context context) {
        this.results = results;
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final Result result = results.get(position);

        holder.dishTv.setText(result.getName());
        holder.servingTv.setText(context.getString(R.string.servings).concat(String.valueOf(result.getServings())));

        if (!result.getImage().isEmpty())
            Glide.with(context).load(result.getImage()).into(holder.recipeImage);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Ingredient> ingredients = result.getIngredients();
                List<Step> steps = result.getSteps();

                Gson gson = new Gson();
                String ingredientJson = gson.toJson(ingredients);
                String stepsJson = gson.toJson(steps);
                String resultJson = gson.toJson(result);
                sharedPreferences.edit().putString(Constants.WIDGET_RESULT, resultJson).apply();

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra(Constants.KEY_INGREDIENTS, ingredientJson);
                intent.putExtra(Constants.KEY_STEPS, stepsJson);

                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        @BindView(R.id.recipe_image)
        ImageView recipeImage;
        @BindView(R.id.dish_tv)
        TextView dishTv;
        @BindView(R.id.serving_tv)
        TextView servingTv;
        @BindView(R.id.card_view)
        CardView cardView;

        public VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
