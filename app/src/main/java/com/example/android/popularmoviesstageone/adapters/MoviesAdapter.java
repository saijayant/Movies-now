package com.example.android.popularmoviesstageone.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesstageone.R;
import com.example.android.popularmoviesstageone.beans.Movie;
import com.example.android.popularmoviesstageone.constants.Constants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by akshayshahane on 08/06/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private List<Movie> mMovieList;
    final private LisItemClickListener mOnclickListener;

    public MoviesAdapter(List<Movie> movieList, LisItemClickListener lisItemClickListener) {
        mMovieList = movieList;
        mOnclickListener = lisItemClickListener;
    }


    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.custom_item_movies, parent, shouldAttachToParentImmediately);

        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {

        if (null != mMovieList.get(position).getMoviePosterUrl()) {
            Picasso.with(holder.mMoviesItem.getContext())
                    .load(Constants.BASE_URL_IMAGES_w342 + mMovieList.get(position)
                            .getMoviePosterUrl())
                    .placeholder(R.drawable.ic_panorama_black_24dp)
                    .error(R.drawable.ic_error_outline_black_24dp)
                    .into(holder.mMoviesItem);
        } else {

            String filename = mMovieList.get(position).getId();

            File picfile = new File(holder.mMoviesItem.getContext().getFilesDir(), filename);

            if (picfile.exists()) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(picfile));
                    holder.mMoviesItem.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    holder.mMoviesItem.setImageResource(R.drawable.ic_error_outline_black_24dp);
                    e.printStackTrace();
                }
            } else {
                holder.mMoviesItem.setImageResource(R.drawable.ic_error_outline_black_24dp);
            }
        }

    }

    @Override
    public int getItemCount() {

        if (mMovieList.isEmpty()) {
            return 0;
        } else {
            return mMovieList.size();
        }
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.movies_item)
        ImageView mMoviesItem;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnclickListener.onListItemClick(getAdapterPosition());

        }
    }


    public interface LisItemClickListener {
        void onListItemClick(int position);

    }
}
