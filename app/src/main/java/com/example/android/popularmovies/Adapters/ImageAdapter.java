package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.android.popularmovies.DataClass.SampleMovie;
import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

/*
Follows the following tutorial to build GridView to display movie posters :
<a> http://developer.android.com/guide/topics/ui/layout/gridview.html</a>
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    public ArrayList<SampleMovie> mMovies;
    private final int mHeight;
    private final int mWidth;

    public ImageAdapter(Context c) {
        mContext = c;
        mMovies = new ArrayList<>();
        mHeight = Math.round(mContext.getResources().getDimension(R.dimen.poster_height));
        mWidth = Math.round(mContext.getResources().getDimension(R.dimen.poster_width));
    }

    public void add(SampleMovie sm){
        mMovies.add(sm);
        notifyDataSetChanged();
    }

    public void addAll(Collection<SampleMovie> xs) {
        mMovies.addAll(xs);
        notifyDataSetChanged();
    }

    public void clearGridView(){
        mMovies.clear();
        notifyDataSetChanged();
    }

    public void addData(Collection<SampleMovie> xs){
        clearGridView();
        for(SampleMovie sm : xs){
            add(sm);
        }
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public SampleMovie getItem(int position) {

        if (position < 0 || position >= mMovies.size()) {
            return null;
        }
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {

        SampleMovie movie = getItem(position);
        if (movie == null) {
            return -1L;
        }

        return movie.id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SampleMovie movie = getItem(position);
        if (movie == null) {
            return null;
        }

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        Uri posterUri = movie.buildUri(mContext.getString(R.string.api_poster_default_size));
        Picasso.with(mContext)
                .load(posterUri)
                .into(imageView);

        return imageView;
    }
}
