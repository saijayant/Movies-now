package com.example.android.popularmoviesstageone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstageone.adapters.ReviewsAdapter;
import com.example.android.popularmoviesstageone.adapters.TrailerAdapter;
import com.example.android.popularmoviesstageone.beans.Reviews;
import com.example.android.popularmoviesstageone.beans.Trailer;
import com.example.android.popularmoviesstageone.constants.Constants;
import com.example.android.popularmoviesstageone.db.FavMoviesContract;
import com.example.android.popularmoviesstageone.networking.ApiInterface;
import com.example.android.popularmoviesstageone.networking.RetrofitClient;
import com.example.android.popularmoviesstageone.utils.Utils;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by akshayshahane on 11/06/17.
 */

public class MoviesDetailsActivity extends AppCompatActivity implements TrailerAdapter.LisItemClickListener, ReviewsAdapter.LisItemClickListener {
    private static final String TAG = "MoviesDetailsActivity";
    private String title;
    private String plotSymopsis;
    private String ratings;
    private String releaseDate;
    private String posterUrl;
    private String movieId;
    @BindView(R.id.tv_movie_description)
    TextView mTvMovieDescription;
    @BindView(R.id.iv_poster)
    ImageView mIvPoster;
    @BindView(R.id.ratings)
    TextView mRatings;
    @BindView(R.id.relese_date)
    TextView mReleseDate;
    @BindView(R.id.view)
    View mView;
    @BindView(R.id.tvMovieTitle)
    TextView mTvMovieTitle;


    private List<Trailer> mTrailerList;
    private List<Reviews> mReviewsList;
    private ReviewsAdapter reviewsAdapter;
    private TrailerAdapter trailerAdapter;
    @BindView(R.id.rv_trailers)
    RecyclerView mRvTrailers;
    @BindView(R.id.textViewReviews)
    TextView mTextViewReviews;
    @BindView(R.id.rv_reviews)
    RecyclerView mRvReviews;
    @BindView(R.id.fab_fav)
    FloatingActionButton mFabFav;


    private boolean isFav = false;
    @BindView(R.id.ratingBar)
    RatingBar mRatingBar;
    @BindView(R.id.tv_ratings)
    TextView mTvRatings;


    @BindView(R.id.textViewTrailers)
    TextView mTextViewTrailers;
    @BindView(R.id.tv_review_error)
    TextView mTvReviewError;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getDataFromInetent();
        isFavMovie();
        getSupportActionBar().setTitle(title);


        if (Utils.isNetworkAvailble(this)){

            mRvTrailers.setVisibility(View.VISIBLE);
            mRvReviews.setVisibility(View.VISIBLE);
            mTextViewReviews.setVisibility(View.VISIBLE);
            mTextViewTrailers.setVisibility(View.VISIBLE);
        }else {
            mRvTrailers.setVisibility(View.GONE);
            mRvReviews.setVisibility(View.GONE);
            mTextViewReviews.setVisibility(View.GONE);
            mTextViewTrailers.setVisibility(View.GONE);
            loadPicLocally();

        }
        mTrailerList = new ArrayList<>();
        mReviewsList = new ArrayList<>();
        mTvMovieTitle.setText(title);
        mTvMovieDescription.setText(plotSymopsis);
        if (TextUtils.isEmpty(posterUrl)){
            loadPicLocally();
        }else {
            Picasso.with(this).load(Constants.BASE_URL_IMAGES_w342 + posterUrl).into(mIvPoster);

        }
        trailerAdapter = new TrailerAdapter(mTrailerList, this);
        mRvTrailers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRvTrailers.setAdapter(trailerAdapter);
        reviewsAdapter = new ReviewsAdapter(mReviewsList, this);
        mRvReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvReviews.setNestedScrollingEnabled(false);
        mRvReviews.setAdapter(reviewsAdapter);

        setRatings();


        if (!(TextUtils.isEmpty(movieId))){
            getListOfTrailers(movieId);
            getReviews(movieId);
        }


        if (isFav) {
            mFabFav.setImageResource(R.drawable.ic_favorite_black_24dp);

        } else {
            mFabFav.setImageResource(R.drawable.ic_favorite_border_black_24dp);

        }


        mFabFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFav) {

                    removeFromFav();


                } else {

                    addToFav();

                }
            }
        });


    }

    private void setRatings() {


        if (!(TextUtils.isEmpty(ratings))) {
            float rating = ((Float.parseFloat(ratings)) / 2);
            mRatingBar.setRating(rating);
            mTvRatings.setText(ratings.concat("/10"));

        }
    }

    private void isFavMovie() {

        Cursor cursor = getContentResolver().query(FavMoviesContract.FavMoviesEntry.CONTENT_URI, new String[]{FavMoviesContract.FavMoviesEntry.COLUMN_MOVIE_ID}, FavMoviesContract.FavMoviesEntry.COLUMN_MOVIE_ID + "=?", new String[]{movieId}, null);
        if (null != cursor) {
            int cursorCount = cursor.getCount();
            if (cursorCount > 0) {
                isFav = true;


            } else {
                isFav = false;
            }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "isFavMovie:  isFav" + isFav);
            }
            cursor.close();

        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "isFavMovie: cursor is null ");
            }
        }
    }

    private void getListOfTrailers(String movieId) {

        if (Utils.isNetworkAvailble(this)) {

            mTrailerList.clear();


            ApiInterface api = RetrofitClient.getClient().create(ApiInterface.class);

            Call<JsonElement> call = api.getTrailersList(movieId, BuildConfig.api_key);

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "onResponse:  response" + response.body().toString());
                    }


                    if (200 == response.code()) {

                        try {

                            JSONObject trailerResponse = new JSONObject(response.body().toString());

                            if (null != trailerResponse) {

                                JSONArray trailersArray = trailerResponse.getJSONArray("results");
                                for (int i = 0; i < trailersArray.length(); i++) {
                                    JSONObject obj = (JSONObject) trailersArray.get(i);

                                    Trailer trailerObj = new Trailer();

                                    trailerObj.setId(obj.getString("id"));
                                    trailerObj.setKey(obj.getString("key"));

                                    mTrailerList.add(trailerObj);

                                }
                            }

                            trailerAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(MoviesDetailsActivity.this, "Something Went Wrong Please try again", Toast.LENGTH_SHORT).show();

                }
            });
        } else {

            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }



    private  void loadPicLocally (){
        String filename = movieId;

        File picfile = new File(this.getFilesDir(), filename);

        if (picfile.exists()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(picfile));
                mIvPoster.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                mIvPoster.setImageResource(R.drawable.ic_error_outline_black_24dp);
                e.printStackTrace();
            }
        } else {
            mIvPoster.setImageResource(R.drawable.ic_error_outline_black_24dp);
        }
    }


    private void getReviews(String movieId) {
        if (Utils.isNetworkAvailble(this)) {
            mReviewsList.clear();
            ApiInterface api = RetrofitClient.getClient().create(ApiInterface.class);

            Call<JsonElement> call = api.getReviews(movieId, BuildConfig.api_key);

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                    if (200 == response.code()) {

                        try {
                            JSONObject reviewsObj = new JSONObject(response.body().toString());

                            if (null != reviewsObj) {

                                JSONArray reviewArray = reviewsObj.getJSONArray("results");



                                if (null!=reviewArray){
                                    mRvReviews.setVisibility(View.VISIBLE);
                                    mTvReviewError.setVisibility(View.GONE);

                                    if (reviewArray.length()==0){
                                        mRvReviews.setVisibility(View.GONE);
                                        mTvReviewError.setVisibility(View.VISIBLE);
                                    }
                                for (int i = 0; i < reviewArray.length(); i++) {

                                    JSONObject obj = (JSONObject) reviewArray.get(i);

                                    Reviews review = new Reviews();

                                    review.setAuthor(obj.getString("author"));
                                    review.setContent(obj.getString("content"));


                                    mReviewsList.add(review);

                                }



                                }else {
                                    mRvReviews.setVisibility(View.GONE);
                                    mTvReviewError.setVisibility(View.VISIBLE);
                                }

                                reviewsAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(MoviesDetailsActivity.this, "Something Went Wrong Please try again", Toast.LENGTH_SHORT).show();

                }
            });

        } else {

            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void getDataFromInetent() {

        if (getIntent().hasExtra("title")) {

            title = getIntent().getStringExtra("title");

        }

        if (getIntent().hasExtra("id")) {
            movieId = getIntent().getStringExtra("id");
        }

        if (getIntent().hasExtra("poster_url")) {
            posterUrl = getIntent().getStringExtra("poster_url");

        }

        if (getIntent().hasExtra("release_date")) {
            releaseDate = getIntent().getStringExtra("release_date");
            mReleseDate.setText(releaseDate);
        }

        if (getIntent().hasExtra("rating")) {
            ratings = getIntent().getStringExtra("rating");
            mRatings.setText("Ratings : " + ratings);
        }

        if (getIntent().hasExtra("plot")) {
            plotSymopsis = getIntent().getStringExtra("plot");
        }
    }

    @Override
    public void onListItemClick(int position) {

    }


    private void addToFav() {
        isFav = true;
        mFabFav.setImageResource(R.drawable.ic_favorite_black_24dp);
        ContentValues values = new ContentValues();
        values.put(FavMoviesContract.FavMoviesEntry.COLUMN_MOVIE_ID, movieId);
        values.put(FavMoviesContract.FavMoviesEntry.COLUMN_MOVIE_TITLE, title);
        values.put(FavMoviesContract.FavMoviesEntry.COLUMN_RDATE, releaseDate);
        values.put(FavMoviesContract.FavMoviesEntry.COLUMN_RATINGS, ratings);
        values.put(FavMoviesContract.FavMoviesEntry.COLUMN_SYNOPSIS, plotSymopsis);


        Uri addToFavUri = getContentResolver().insert(FavMoviesContract.FavMoviesEntry.CONTENT_URI, values);


        Bitmap bitmap = ((BitmapDrawable) mIvPoster.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        String filename = movieId;
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(byteArray);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Toast.makeText(MoviesDetailsActivity.this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();

    }

    private void removeFromFav() {
        isFav = false;
        mFabFav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        int rowsDeleted = getContentResolver().delete(FavMoviesContract.FavMoviesEntry.CONTENT_URI, FavMoviesContract.FavMoviesEntry.COLUMN_MOVIE_ID + " = ?", new String[]{movieId});

        if (rowsDeleted > 0) {
            isFav = false;
            Toast.makeText(this, R.string.removed_from_favs, Toast.LENGTH_SHORT).show();
        }

    }
}
