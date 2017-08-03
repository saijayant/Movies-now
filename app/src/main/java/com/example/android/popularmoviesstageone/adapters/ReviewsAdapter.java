package com.example.android.popularmoviesstageone.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstageone.R;
import com.example.android.popularmoviesstageone.beans.Reviews;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by akshayshahane on 08/06/17.
 */

abstract class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MoviesAdapterViewHolder> {


    private List<Reviews> mReviewsList;
    final private LisItemClickListener mOnclickListener;

    public ReviewsAdapter(List<Reviews> reviewsList, LisItemClickListener lisItemClickListener) {
        mReviewsList = reviewsList;
        mOnclickListener = lisItemClickListener;
    }


    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.reviews_item, parent, shouldAttachToParentImmediately);

        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {


        String author = mReviewsList.get(position).getAuthor();
        String comment = mReviewsList.get(position).getContent();

        if (!(TextUtils.isEmpty(author))){
            holder.mTvAuthor.setText(author);
        }

        if (!(TextUtils.isEmpty(comment))){
            holder.mTvComments.setText(comment);
        }




    }

    @Override
    public int getItemCount() {

        if (mReviewsList.isEmpty()) {
            return 0;
        } else {
            return mReviewsList.size();
        }
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_author)
        TextView mTvAuthor;
        @BindView(R.id.tv_comments)
        TextView mTvComments;

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
