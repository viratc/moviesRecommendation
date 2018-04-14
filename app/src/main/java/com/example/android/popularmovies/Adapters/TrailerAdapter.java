package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.DataClass.Trailers;
import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by virat on 15/04/16.
 */
public class TrailerAdapter extends BaseAdapter {

    private Context mcontext;
    public ArrayList<Trailers> mTrailers;
    public LayoutInflater inflater;

    public TrailerAdapter(Context context){
        this.mcontext=context;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        mTrailers = new ArrayList<>();
    }

    public void add(ArrayList<Trailers> t){
        mTrailers.addAll(t);
        notifyDataSetChanged();
    }

    public void clear(ArrayList<Trailers> t){
        mTrailers.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTrailers.size();
    }

    @Override
    public Trailers getItem(int position) {
        if(position < 0 || position>mTrailers.size()){
            return null;
        }
        return mTrailers.get(position);
    }

    @Override
    public long getItemId(int position){

        Trailers trailer = getItem(position);
        if (trailer == null){
            return -1L;
        }
        return position;
        //return Integer.parseInt(trailer.getTrailerId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.fragment_movie_trailer,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        Trailers t = getItem(position);

        viewHolder = (ViewHolder)convertView.getTag();

        //http://stackoverflow.com/questions/2068344/how-do-i-get-a-youtube-video-thumbnail-from-the-youtube-api
        String Url_Thumbnail = "http://img.youtube.com/vi/" + t.getTrailerKey() + "/0.jpg";

        Picasso.with(mcontext)
                .load(Url_Thumbnail)
                .into(viewHolder.imageView);

        return convertView;
    }

    public class ViewHolder{
        ImageView imageView;
        public ViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.trailer_image);
        }
    }
}
