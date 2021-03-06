package com.example.primevideoclone.retrofit;

import com.example.primevideoclone.model.AllCategory;
import com.example.primevideoclone.model.BannerMovies;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("banner_movie.json")
    Observable<List<BannerMovies>> getAllBanners();

    @GET("{categoryId}/all_movies.json")
    Observable<List<AllCategory>> getAllCategoryMovies(@Path("categoryId")int categoryId);
}
