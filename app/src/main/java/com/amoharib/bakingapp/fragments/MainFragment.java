package com.amoharib.bakingapp.fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amoharib.bakingapp.activities.MainActivity;
import com.amoharib.bakingapp.R;
import com.amoharib.bakingapp.adapters.RecipeAdapter;
import com.amoharib.bakingapp.model.Result;
import com.amoharib.bakingapp.util.ApiInterface;
import com.amoharib.bakingapp.util.Constants;
import com.amoharib.bakingapp.util.NetworkUtils;
import com.amoharib.bakingapp.util.SimpleIdlingResource;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    @BindView(R.id.recipe_recycler)
    RecyclerView recipeRecycler;
    @BindView(R.id.progress)
    ProgressBar progress;
    Unbinder unbinder;

    private RecipeAdapter adapter;
    private ApiInterface apiInterface;
    private List<Result> results;
    private SimpleIdlingResource idlingResource;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        apiInterface = Constants.getInstance().create(ApiInterface.class);
        idlingResource = (SimpleIdlingResource) ((MainActivity) getActivity()).getIdlingResource();
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        if (MainActivity.isTab) {
            recipeRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        } else {
            recipeRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        }


        if (NetworkUtils.checkConnectivity(getActivity())) {
            getResult();
        } else {
            Snackbar.make(view, R.string.check_connection_message, Snackbar.LENGTH_LONG).show();
        }

        return view;
    }

    private void getResult() {
        apiInterface.getResults()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Result>>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<Result> results) {
                        MainFragment.this.results = results;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", e.getMessage());
                        showError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        if (!disposable.isDisposed()) {
                            disposable.dispose();
                        }
                        progress.setVisibility(View.GONE);
                        adapter = new RecipeAdapter(results, getActivity());
                        recipeRecycler.setAdapter(adapter);
                        idlingResource.setIdleState(true);
                    }
                });
    }

    private void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
