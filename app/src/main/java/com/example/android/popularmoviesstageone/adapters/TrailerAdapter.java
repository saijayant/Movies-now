package com.example.android.popularmoviesstageone.adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstageone.R;
import com.example.android.popularmoviesstageone.beans.Trailer;
import com.example.android.popularmoviesstageone.constants.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by akshayshahane on 08/06/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MoviesAdapterViewHolder> {



    private List<Trailer> mTrailerList;
    final private LisItemClickListener mOnclickListener;

    public TrailerAdapter(List<Trailer> trailerList, LisItemClickListener lisItemClickListener) {
        mTrailerList = trailerList;
        mOnclickListener = lisItemClickListener;
    }


    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.trailers_item, parent, shouldAttachToParentImmediately);

        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesAdapterViewHolder holder, final int position) {


        final String key = mTrailerList.get(position).getKey();
        String thumbUrl = Constants.BASE_URL_YOUTUBE_THUMBNAIL.concat(key).concat("/hqdefault.jpg");

        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareYoutubeLink(Constants.BASE_URL_YOUTUBE_VID.concat(key),getActivity(holder.mIvPlay.getContext()));
            }
        });

        holder.mIvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openYoutubeLink(key,view.getContext());
            }
        });


        Picasso.with(holder.mIvTrailerThumb.getContext())
                .load(thumbUrl)
                .placeholder(R.drawable.ic_panorama_black_24dp)
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(holder.mIvTrailerThumb);

    }

    @Override
    public int getItemCount() {

        if (mTrailerList.isEmpty()) {
            return 0;
        } else {
            return mTrailerList.size();
        }
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_trailer_thumb)
        ImageView mIvTrailerThumb;
        @BindView(R.id.ivPlay)
        ImageView mIvPlay;

        @BindView(R.id.textView)
        TextView mTextView;

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
    private Activity getActivity(Context contextPassed) {
        Context context = contextPassed;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }


    private void shareYoutubeLink(String link,Context context){

        ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from((Activity) context);
        Intent intent = intentBuilder
                .setType("text/plain")
                .setText(link)
                .setChooserTitle("Choose to share trailer link")
                .createChooserIntent();

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }


    private void openYoutubeLink(String key,Context context ){
        Intent applicationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
           context.startActivity(applicationIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(browserIntent);
        }

    }
}
