package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.DataClass.Reviews;
import com.example.android.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by virat on 15/04/16.
 */
public class ReviewsAdapter extends BaseAdapter {

    public static final String LOG_TAG = ReviewsAdapter.class.getSimpleName();

    private Context mcontext;
    public List<Reviews> mReviews;
    public final LayoutInflater inflater;

    public ReviewsAdapter(Context c){
        this.mcontext = c;
        mReviews = new ArrayList<>();
        inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE); ;
    }

    public void add(ArrayList<Reviews> r){
        mReviews.addAll(r);
        notifyDataSetChanged();
    }

    public void clear(){
        mReviews.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mReviews.size();
    }

    @Override
    public Reviews getItem(int position) {

        if(position < 0 || mReviews.size()<position){
            return null;
        }

        return mReviews.get(position);
    }

    @Override
    public long getItemId(int position) {

        Reviews r = getItem(position);
        if(r==null){
            return -1L;
        }
        return position;
        //return Long.valueOf(r.getReviewsID());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.fragment_movie_review,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        Reviews review = getItem(position);

        viewHolder = (ViewHolder) convertView.getTag();

        Log.d(LOG_TAG, "Author: " + review.getReviewsAuthor());
        viewHolder.author.setText(review.getReviewsAuthor());

        Log.d(LOG_TAG, "Content: " + review.getReviewsContent());
        viewHolder.content.setText(Html.fromHtml(review.getReviewsContent()));

        return convertView;
    }

    public class ViewHolder{

        TextView author;
        TextView content;

        public ViewHolder(View view){
            author = (TextView) view.findViewById(R.id.review_author);
            content = (TextView) view.findViewById(R.id.review_content);
        }
    }
}
