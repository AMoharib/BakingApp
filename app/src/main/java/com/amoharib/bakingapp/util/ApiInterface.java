package com.amoharib.bakingapp.util;

import com.amoharib.bakingapp.model.Result;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("topher/2017/May/59121517_baking/baking.json")
    Observable<List<Result>> getResults();

}
