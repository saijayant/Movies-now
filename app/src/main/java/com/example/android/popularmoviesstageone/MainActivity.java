package com.example.android.popularmoviesstageone;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstageone.adapters.MoviesAdapter;
import com.example.android.popularmoviesstageone.beans.Movie;
import com.example.android.popularmoviesstageone.custom.RichBottomNavigationView;
import com.example.android.popularmoviesstageone.db.FavMoviesContract;
import com.example.android.popularmoviesstageone.networking.ApiInterface;
import com.example.android.popularmoviesstageone.networking.RetrofitClient;
import com.example.android.popularmoviesstageone.utils.Utils;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.LisItemClickListener {

    private static final String TAG = "MainActivity";
    @BindView(R.id.rv_movies)
    RecyclerView mRvMovies;
    @BindView(R.id.tv_error_message_display)
    TextView mTvErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mPbLoadingIndicator;
    @BindView(R.id.bottomNavigationView)
    RichBottomNavigationView mBottomNavigationView;
    private int positionBottomMenu = 0;
    private ArrayList<Movie> moviesList;
    private ArrayList<Movie> popularMoviesList;
    private ArrayList<Movie> favoriteMoviesList;

    private MoviesAdapter mAdapter;

    private GridLayoutManager gridLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.top_rated);
        ButterKnife.bind(this);


        gridLayoutManager = new GridLayoutManager(this, 3);
        mRvMovies.setLayoutManager(gridLayoutManager);
        moviesList = new ArrayList<>();
        popularMoviesList = new ArrayList<>();
        favoriteMoviesList = new ArrayList<>();
        mAdapter = new MoviesAdapter(moviesList, this);
        mRvMovies.setAdapter(mAdapter);


        handleNavigationClicks(savedInstanceState);

        if (savedInstanceState != null) {
            positionBottomMenu = savedInstanceState.getInt("selectedItem");
            moviesList = savedInstanceState.getParcelableArrayList("top");
            popularMoviesList = savedInstanceState.getParcelableArrayList("popular");
            favoriteMoviesList = savedInstanceState.getParcelableArrayList("fav");


        } else {
            getListOfTopRatedMovies();

        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (positionBottomMenu == 2) {
            loadFavMovies();
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedItem", positionBottomMenu);
        outState.putParcelableArrayList("top", moviesList);
        outState.putParcelableArrayList("popular", popularMoviesList);
        outState.putParcelableArrayList("fav", favoriteMoviesList);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        moviesList = savedInstanceState.getParcelableArrayList("top");
        popularMoviesList = savedInstanceState.getParcelableArrayList("popular");

    }

    private void handleNavigationClicks(final Bundle b) {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.action_top_rated:
                        mRvMovies.setAdapter(mAdapter);
                        if (b == null) {
                            if (Utils.isNetworkAvailble(MainActivity.this)) {
                                getSupportActionBar().setTitle(R.string.top_rated);

                                getListOfTopRatedMovies();
                            } else {
                                getSupportActionBar().setTitle(R.string.top_rated);

                                Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                                moviesList.clear();
                                mAdapter.notifyDataSetChanged();
                            }
                            positionBottomMenu = 0;
                        } else {
                            getSupportActionBar().setTitle(R.string.top_rated);
                            mRvMovies.setVisibility(View.VISIBLE);
                            mTvErrorMessageDisplay.setVisibility(View.INVISIBLE);

                            if (popularMoviesList.size() < 1) {


                                getListOfTopRatedMovies();
                            } else {
                                mAdapter = new MoviesAdapter(moviesList, MainActivity.this);
                                mRvMovies.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            }

                        }
                        break;

                    case R.id.action_popular:
//
                        mAdapter = new MoviesAdapter(popularMoviesList, MainActivity.this);
                        mRvMovies.setAdapter(mAdapter);
                        if (b == null) {
                            if (Utils.isNetworkAvailble(MainActivity.this)) {
                                getSupportActionBar().setTitle(R.string.popular_movies);

                                getListOfPopularMovies();
                            } else {
                                getSupportActionBar().setTitle(R.string.popular_movies);

                                moviesList.clear();
                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                            }
                            positionBottomMenu = 1;
                        } else {
                            getSupportActionBar().setTitle(R.string.popular_movies);

                            if (popularMoviesList.size() < 1) {


                                getListOfPopularMovies();
                            } else {
                                mRvMovies.setVisibility(View.VISIBLE);
                                mTvErrorMessageDisplay.setVisibility(View.INVISIBLE);

                                mAdapter.notifyDataSetChanged();
                            }

                        }

                        break;

                    case R.id.action_favorites:
                        mAdapter = new MoviesAdapter(favoriteMoviesList, MainActivity.this);
                        mRvMovies.setAdapter(mAdapter);
                        getSupportActionBar().setTitle(R.string.favorites);

                        loadFavMovies();
                        positionBottomMenu = 2;
                        break;
                }
                return true;
            }
        });
    }

    private void getListOfTopRatedMovies() {
        if (Utils.isNetworkAvailble(this)) {
            moviesList.clear();
            mPbLoadingIndicator.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mRvMovies.setVisibility(View.VISIBLE);
            mTvErrorMessageDisplay.setVisibility(View.INVISIBLE);
            ApiInterface api = RetrofitClient.getClient().create(ApiInterface.class);
            Call<JsonElement> call = api.getTopRatedMovies(BuildConfig.api_key);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "onResponse:  response" + response.body().toString());
                    }
                    if (response.code() == 200) {

                        try {
                            JSONObject moviesResponse = new JSONObject(response.body().toString());

                            JSONArray moviesArray = moviesResponse.getJSONArray("results");


                            for (int i = 0; i < moviesArray.length(); i++) {

                                Movie movieData = new Movie();
                                JSONObject movieObj = moviesArray.getJSONObject(i);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onResponse: " + movieObj.getString("poster_path"));
                                }
                                movieData.setMoviePosterUrl(movieObj.getString("poster_path"));
                                movieData.setTitle(movieObj.getString("title"));
                                movieData.setReleaseDate(movieObj.getString("release_date"));
                                movieData.setRatings(movieObj.getString("vote_average"));
                                movieData.setPlotSynopsis(movieObj.getString("overview"));
                                movieData.setId(movieObj.getString("id"));
                                moviesList.add(movieData);


                            }

                            mAdapter.notifyDataSetChanged();
                            mPbLoadingIndicator.setVisibility(View.INVISIBLE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            if (BuildConfig.DEBUG) {

                                Log.d(TAG, "onResponse: " + moviesResponse.getJSONArray("results").length());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mPbLoadingIndicator.setVisibility(View.INVISIBLE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            mTvErrorMessageDisplay.setVisibility(View.VISIBLE);
                            mRvMovies.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        mRvMovies.setVisibility(View.INVISIBLE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        mTvErrorMessageDisplay.setVisibility(View.VISIBLE);
                        mPbLoadingIndicator.setVisibility(View.INVISIBLE);

                    }

                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {

                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "onFailure: " + t.toString());
                    }
                    mRvMovies.setVisibility(View.INVISIBLE);
                    mTvErrorMessageDisplay.setVisibility(View.VISIBLE);
                    mPbLoadingIndicator.setVisibility(View.INVISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                }
            });

        } else {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void getListOfPopularMovies() {
        if (Utils.isNetworkAvailble(this)) {
            mTvErrorMessageDisplay.setVisibility(View.INVISIBLE);
            mRvMovies.setVisibility(View.VISIBLE);
            popularMoviesList.clear();
            mPbLoadingIndicator.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mTvErrorMessageDisplay.setVisibility(View.INVISIBLE);
            ApiInterface api = RetrofitClient.getClient().create(ApiInterface.class);
            Call<JsonElement> call = api.getPopularMovies(BuildConfig.api_key);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "onResponse:  response" + response.body().toString());
                    }

                    if (response.code() == 200) {

                        try {
                            JSONObject moviesResponse = new JSONObject(response.body().toString());

                            JSONArray moviesArray = moviesResponse.getJSONArray("results");


                            for (int i = 0; i < moviesArray.length(); i++) {

                                Movie movieData = new Movie();
                                JSONObject movieObj = moviesArray.getJSONObject(i);

                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onResponse: " + movieObj.getString("poster_path"));
                                    Log.d(TAG, "onResponse: title " + movieObj.getString("title"));
                                }
                                movieData.setMoviePosterUrl(movieObj.getString("poster_path"));
                                movieData.setTitle(movieObj.getString("title"));
                                movieData.setReleaseDate(movieObj.getString("release_date"));
                                movieData.setRatings(movieObj.getString("vote_average"));
                                movieData.setPlotSynopsis(movieObj.getString("overview"));
                                movieData.setId(movieObj.getString("id"));
                                popularMoviesList.add(movieData);


                            }

                            mAdapter.notifyDataSetChanged();
                            mPbLoadingIndicator.setVisibility(View.INVISIBLE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                            if (BuildConfig.DEBUG) {

                                Log.d(TAG, "onResponse: " + moviesResponse.getJSONArray("results").length());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mPbLoadingIndicator.setVisibility(View.INVISIBLE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            mRvMovies.setVisibility(View.GONE);

                            mTvErrorMessageDisplay.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mPbLoadingIndicator.setVisibility(View.INVISIBLE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        mRvMovies.setVisibility(View.GONE);
                        mTvErrorMessageDisplay.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {

                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "onFailure: " + t.toString());
                        mRvMovies.setVisibility(View.GONE);
                        mTvErrorMessageDisplay.setVisibility(View.VISIBLE);
                        mPbLoadingIndicator.setVisibility(View.INVISIBLE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    }

                }
            });

        } else {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onListItemClick(int position) {

       switch (positionBottomMenu){

           case 0 :
               Intent i = new Intent(MainActivity.this, MoviesDetailsActivity.class);
               i.putExtra("title", moviesList.get(position).getTitle());
               i.putExtra("poster_url", moviesList.get(position).getMoviePosterUrl());
               i.putExtra("release_date", moviesList.get(position).getReleaseDate());
               i.putExtra("rating", moviesList.get(position).getRatings());
               i.putExtra("plot", moviesList.get(position).getPlotSynopsis());
               i.putExtra("id", moviesList.get(position).getId());
               startActivity(i);
               break;


           case 1 :
               Intent iPopular = new Intent(MainActivity.this, MoviesDetailsActivity.class);
               iPopular.putExtra("title", popularMoviesList.get(position).getTitle());
               iPopular.putExtra("poster_url", popularMoviesList.get(position).getMoviePosterUrl());
               iPopular.putExtra("release_date", popularMoviesList.get(position).getReleaseDate());
               iPopular.putExtra("rating", popularMoviesList.get(position).getRatings());
               iPopular.putExtra("plot", popularMoviesList.get(position).getPlotSynopsis());
               iPopular.putExtra("id", popularMoviesList.get(position).getId());
               startActivity(iPopular);
               break;

           case 2 :
               Intent iFav = new Intent(MainActivity.this, MoviesDetailsActivity.class);
               iFav.putExtra("title", favoriteMoviesList.get(position).getTitle());
               iFav.putExtra("poster_url", favoriteMoviesList.get(position).getMoviePosterUrl());
               iFav.putExtra("release_date", favoriteMoviesList.get(position).getReleaseDate());
               iFav.putExtra("rating", favoriteMoviesList.get(position).getRatings());
               iFav.putExtra("plot", favoriteMoviesList.get(position).getPlotSynopsis());
               iFav.putExtra("id", favoriteMoviesList.get(position).getId());
               startActivity(iFav);

       }




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPbLoadingIndicator.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }


    private void loadFavMovies() {
        favoriteMoviesList.clear();

        Cursor cursor = getContentResolver().query(FavMoviesContract.FavMoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                FavMoviesContract.FavMoviesEntry.COLUMN_MOVIE_ID);

        if (null != cursor) {
            mPbLoadingIndicator.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mRvMovies.setVisibility(View.VISIBLE);
            mTvErrorMessageDisplay.setVisibility(View.INVISIBLE);
            if (cursor.getCount() > 0) {


                while (cursor.moveToNext()) {


                    int titleColumnIndex = cursor.getColumnIndex(FavMoviesContract.FavMoviesEntry.COLUMN_MOVIE_TITLE);
                    int movieIdColumnIndex = cursor.getColumnIndex(FavMoviesContract.FavMoviesEntry.COLUMN_MOVIE_ID);
                    int releaseDateColumnIndex = cursor.getColumnIndex(FavMoviesContract.FavMoviesEntry.COLUMN_RDATE);
                    int ratingsColumnIndex = cursor.getColumnIndex(FavMoviesContract.FavMoviesEntry.COLUMN_RATINGS);
                    int synopsisColumnIndex = cursor.getColumnIndex(FavMoviesContract.FavMoviesEntry.COLUMN_SYNOPSIS);

                    Movie movie = new Movie();
                    movie.setTitle(cursor.getString(titleColumnIndex));
                    movie.setId(cursor.getString(movieIdColumnIndex));
                    movie.setReleaseDate(cursor.getString(releaseDateColumnIndex));
                    movie.setRatings(cursor.getString(ratingsColumnIndex));
                    movie.setPlotSynopsis(cursor.getString(synopsisColumnIndex));

                    favoriteMoviesList.add(movie);


                }

                mAdapter.notifyDataSetChanged();
                mPbLoadingIndicator.setVisibility(View.INVISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            } else {
                mRvMovies.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                mTvErrorMessageDisplay.setVisibility(View.VISIBLE);
                mTvErrorMessageDisplay.setText(R.string.favorites_msg);
                mPbLoadingIndicator.setVisibility(View.INVISIBLE);

            }
        }


    }
}
