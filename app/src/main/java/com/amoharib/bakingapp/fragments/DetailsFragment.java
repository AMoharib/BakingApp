package com.amoharib.bakingapp.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amoharib.bakingapp.R;
import com.amoharib.bakingapp.activities.MainActivity;
import com.amoharib.bakingapp.activities.VideoActivity;
import com.amoharib.bakingapp.adapters.StepsAdapter;
import com.amoharib.bakingapp.model.Ingredient;
import com.amoharib.bakingapp.model.Step;
import com.amoharib.bakingapp.util.ClickCallBack;
import com.amoharib.bakingapp.util.Constants;
import com.amoharib.bakingapp.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment implements ClickCallBack {


    @BindView(R.id.ingredient_tv)
    TextView ingredientTv;
    @BindView(R.id.steps_recycler)
    RecyclerView stepsRecycler;
    Unbinder unbinder;

    private StepsAdapter adapter;

    private String ingredientJson, stepsJson;
    private List<Step> steps;
    private List<Ingredient> ingredients;
    private Parcelable listState;
    private LinearLayoutManager layoutManager;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            ingredientJson = bundle.getString(Constants.KEY_INGREDIENTS);
            stepsJson = bundle.getString(Constants.KEY_STEPS);
        }
        Gson gson = new Gson();
        ingredients = gson.fromJson(ingredientJson, new TypeToken<List<Ingredient>>() {
        }.getType());
        steps = gson.fromJson(stepsJson, new TypeToken<List<Step>>() {
        }.getType());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        unbinder = ButterKnife.bind(this, view);

        ingredientTv.setText(StringUtils.concatString(ingredients));

        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(Constants.RECYCLER_VIEW_STATE);
        }

        layoutManager = new LinearLayoutManager(getActivity());

        adapter = new StepsAdapter(steps, getActivity());
        adapter.setClickCallBack(this);
        stepsRecycler.setLayoutManager(layoutManager);
        stepsRecycler.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listState != null) {
            layoutManager.onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.RECYCLER_VIEW_STATE, layoutManager.onSaveInstanceState());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(Step step, int position) {
        if (MainActivity.isTab) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.KEY_STEPS_ID, step.getId());
            bundle.putString(Constants.KEY_STEPS_DESC, step.getDescription());
            bundle.putString(Constants.KEY_STEPS_URL, step.getVideoURL());
            VideoFragment videoFragment = new VideoFragment();
            videoFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.video_fragment_container, videoFragment)
                    .commit();
        } else {
            Intent intent = new Intent(getActivity(), VideoActivity.class);
            intent.putExtra(Constants.KEY_STEPS_JSON, stepsJson);
            intent.putExtra(Constants.KEY_STEP_POSITION, position);
            startActivity(intent);
        }
    }
}
