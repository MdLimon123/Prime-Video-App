package com.example.primevideoclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.primevideoclone.adapter.BannerMoviesPagerAdapter;
import com.example.primevideoclone.adapter.MainRecyclerAdapter;
import com.example.primevideoclone.model.AllCategory;
import com.example.primevideoclone.model.BannerMovies;
import com.example.primevideoclone.model.CategoryItemList;
import com.example.primevideoclone.retrofit.RetrofitClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    BannerMoviesPagerAdapter bannerMoviesPagerAdapter;
    TabLayout indicatorTab, categoryTab;
    ViewPager bannerMovieViewPager;
    List<BannerMovies> homeBannerList;
    List<BannerMovies> tvShowBannerList;
    List<BannerMovies> movieBannerList;
    List<BannerMovies> kidsBannerList;
    Timer sliderTimer;

    NestedScrollView nestedScrollView;
    AppBarLayout appBarLayout;


    MainRecyclerAdapter mainRecyclerAdapter;
    RecyclerView mainRecycler;
    List<AllCategory> allCategoryList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        indicatorTab = findViewById(R.id.tab_indicator);
        categoryTab = findViewById(R.id.tabLayout);
        nestedScrollView = findViewById(R.id.nested_scroll);
        appBarLayout = findViewById(R.id.appbar);


        
        homeBannerList = new ArrayList<>();
        tvShowBannerList = new ArrayList<>();
        movieBannerList = new ArrayList<>();
        kidsBannerList = new ArrayList<>();


        getBannerData();
        getAllMoviesData(1);



        categoryTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 1:
                        setScrollDefaultState();
                        setBannerMoviesPagerAdapter(tvShowBannerList);
                        getAllMoviesData(2);
                        return;
                    case 2:
                        setScrollDefaultState();
                        setBannerMoviesPagerAdapter(movieBannerList);
                        getAllMoviesData(3);
                        return;

                    case 3:
                        setScrollDefaultState();
                        setBannerMoviesPagerAdapter(kidsBannerList);
                        getAllMoviesData(4);
                        return;

                    default:
                        setScrollDefaultState();
                        setBannerMoviesPagerAdapter(homeBannerList);
                        getAllMoviesData(1);


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        allCategoryList = new ArrayList<>();


    }

    private void setBannerMoviesPagerAdapter(List<BannerMovies> bannerMoviesList){

        bannerMovieViewPager = findViewById(R.id.banner_viewPager);
        bannerMoviesPagerAdapter = new BannerMoviesPagerAdapter(this, bannerMoviesList);
        bannerMovieViewPager.setAdapter(bannerMoviesPagerAdapter);
        indicatorTab.setupWithViewPager(bannerMovieViewPager);

         sliderTimer = new Timer();
        sliderTimer.scheduleAtFixedRate(new AutoSlider(),4000,8000);
        indicatorTab.setupWithViewPager(bannerMovieViewPager,true);

    }

    class AutoSlider extends TimerTask{

        @Override
        public void run() {

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    if (bannerMovieViewPager.getCurrentItem() < homeBannerList.size() - 1){
                        bannerMovieViewPager.setCurrentItem(bannerMovieViewPager.getCurrentItem() + 1);


                    }else {

                        bannerMovieViewPager.setCurrentItem(0);


                    }



                }
            });


        }
    }


    public void setMainRecycler(List<AllCategory> allCategoryList){
        mainRecycler = findViewById(R.id.main_recycler);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        mainRecycler.setLayoutManager(layoutManager);
        mainRecyclerAdapter = new MainRecyclerAdapter(this,allCategoryList);
        mainRecycler.setAdapter(mainRecyclerAdapter);
    }

    private void setScrollDefaultState(){

        nestedScrollView.fullScroll(View.FOCUS_UP);
        nestedScrollView.scrollTo(0,0);
        appBarLayout.setExpanded(true);


    }
    private void getBannerData(){

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(RetrofitClient.getRetrofitClien().getAllBanners()
                            .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<BannerMovies>>() {
                    @Override
                    public void onNext(@NonNull List<BannerMovies> bannerMovies) {
                      //  Log.d("bannerData",""+bannerMovies);

                        for(int i = 0; i< bannerMovies.size();i++){

                            if (bannerMovies.get(i).getBannerCategoryId().toString().equals("1")){

                                homeBannerList.add(bannerMovies.get(i));

                            } else if (bannerMovies.get(i).getBannerCategoryId().toString().equals("2")){
                                tvShowBannerList.add(bannerMovies.get(i));

                            }
                            else if (bannerMovies.get(i).getBannerCategoryId().toString().equals("3")){

                                movieBannerList.add(bannerMovies.get(i));

                            }
                            else if (bannerMovies.get(i).getBannerCategoryId().toString().equals("4")){

                                kidsBannerList.add(bannerMovies.get(i));

                            }
                            else {


                            }
                        }


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("bannerData",""+e);

                    }

                    @Override
                    public void onComplete() {

                        setBannerMoviesPagerAdapter(homeBannerList);

                    }
                })


        );

    }


    private void getAllMoviesData(int categoryId){

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(RetrofitClient.getRetrofitClien().getAllCategoryMovies(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<AllCategory>>() {
                    @Override
                    public void onNext(@NonNull List<AllCategory> allCategoryList) {
                        //  Log.d("bannerData",""+bannerMovies);

                        setMainRecycler(allCategoryList);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("bannerData",""+e);

                    }

                    @Override
                    public void onComplete() {



                    }
                })


        );

    }

}