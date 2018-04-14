package com.example.android.popularmovies.DataClass;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.android.popularmovies.MainActivityFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by virat on 23/11/15.
 */

public class SampleMovie implements Parcelable {

    private final String LOG_TAG = SampleMovie.class.getSimpleName();

    public static final String TMDb_ID = "id";
    public static final String TMDb_TITLE = "title";
    public static final String TMDb_OVERVIEW = "overview";
    public static final String TMDb_POSTER_PATH = "poster_path";
    public static final String TMDb_VOTE_AVERAGE = "vote_average";
    public static final String TMDb_RELEASE_DATE = "release_date";

    public int id;
    public String title;
    public String overview;
    public String poster_path;
    public int vote_average;
    public String release_date;

    public SampleMovie(){}

    /*
    Method to extract all the relevant movie data
     */
    public SampleMovie(JSONObject jsonObject) throws JSONException {

                this.id=jsonObject.getInt(TMDb_ID);
                this.title=jsonObject.getString(TMDb_TITLE);
                this.overview=jsonObject.getString(TMDb_OVERVIEW);
                this.poster_path=jsonObject.getString(TMDb_POSTER_PATH);
                this.vote_average=jsonObject.getInt(TMDb_VOTE_AVERAGE);
                this.release_date=jsonObject.getString(TMDb_RELEASE_DATE);
    }

    public SampleMovie(Cursor c){

        this.id = c.getInt(MainActivityFragment.COLUMN_MOVIE_ID);
        this.title = c.getString(MainActivityFragment.COLUMN_TITLE);
        this.overview = c.getString(MainActivityFragment.COLUMN_SYNOPSIS);
        this.poster_path = c.getString(MainActivityFragment.COLUMN_POSTER);
        this.vote_average = c.getInt(MainActivityFragment.COLUMN_RATING);
        this.release_date = c.getString(MainActivityFragment.COLUMN_RELEASEDATE);
    }

    private SampleMovie(Parcel parcel){

        id = parcel.readInt();
        title = parcel.readString();
        overview = parcel.readString();
        poster_path = parcel.readString();
        vote_average = parcel.readInt();
        release_date = parcel.readString();
    }

    /* Method to get movie ID
     */
    public int getMovieID(){
        return id;
    }

    public String getMovieTitle(){
        Log.d(LOG_TAG,"MovieTitle: " +title);
        return title;
    }

    public String getMovieOverview(){
        return overview;
    }

    public String getMoviePoster(){
        return poster_path;
    }

    public int getMovieRating(){
        return vote_average;
    }

    public String getMovieDate(){
        return release_date;
    }

    public String getRating() {
        return "" + vote_average + " / 10";
    }

    /*
    Method to build the required URL, for extracting movies data from TMDb
     */
    public Uri buildUri(String size) {
        final String BASE_URL = "http://image.tmdb.org/t/p/";

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(size)
                .appendEncodedPath(getMoviePoster())
                .build();

        return uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(poster_path);
        dest.writeInt(vote_average);
        dest.writeString(release_date);
    }

    public static final Parcelable.Creator<SampleMovie> CREATOR= new Parcelable.Creator<SampleMovie>(){

        @Override
        public SampleMovie createFromParcel(Parcel source) {
            return new SampleMovie(source);
        }

        @Override
        public SampleMovie[] newArray(int size) {
            return new SampleMovie[size];
        }
    };

}
