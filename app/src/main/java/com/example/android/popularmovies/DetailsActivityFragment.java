package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Adapters.ReviewsAdapter;
import com.example.android.popularmovies.Adapters.TrailerAdapter;
import com.example.android.popularmovies.Data.MoviesContract;
import com.example.android.popularmovies.DataClass.Reviews;
import com.example.android.popularmovies.DataClass.SampleMovie;
import com.example.android.popularmovies.DataClass.Trailers;
import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsActivityFragment extends Fragment {

    public static final String LOG_TAG = DetailsActivityFragment.class.getSimpleName();

    private android.support.v7.widget.ShareActionProvider mShareActionProvider;
    private Toast t;
    private String MOVIE_HASHTAG = "#TMDb Movies";
    private Reviews rv;
    private Trailers trail;
    private SampleMovie mv;
    private ReviewsAdapter mReviewsAdapter;
    private TrailerAdapter mTrailerAdapter;
    static final String MOVIE_DETAILS="MOVIE_DETAILS";

    public DetailsActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if(mv != null) {
            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(R.menu.menu_details, menu);

            //Locate menu item with ShareAction Provider
            MenuItem share = menu.findItem(R.id.action_share);
            final MenuItem favorite = menu.findItem(R.id.rating_bar);

            new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... params) {
                    return favoriteMovies(getActivity(), mv.getMovieID());
                }

                @Override
                protected void onPostExecute(Integer isfavoritemovie) {

                    favorite.setIcon(
                            isfavoritemovie == 1 ?
                                    R.drawable.abc_btn_rating_star_on_mtrl_alpha :
                                    R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                }
            }.execute();

            //Fetch & store ShareAction Provider
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);

            if (trail != null) {
                mShareActionProvider.setShareIntent(setIntent());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.rating_bar:
                ratingBar(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Call to update the share intent
    private Intent setIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mv.getMovieTitle() + " " +
        "http://www.youtube.com/watch?v=" + trail.getTrailerKey());
        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle b = getArguments();
        if(b != null){
            mv = b.getParcelable(DetailsActivityFragment.MOVIE_DETAILS);
        }

        View view = inflater.inflate(R.layout.fragment_details, container, false);

        TextView title = (TextView) view.findViewById(R.id.Title);
        TextView overview = (TextView) view.findViewById(R.id.Overview);
        ImageView poster = (ImageView) view.findViewById(R.id.Poster);
        TextView rating = (TextView) view.findViewById(R.id.Rating);
        TextView release = (TextView) view.findViewById(R.id.ReleaseDate);

        if(mv != null) {
            title.setText(mv.getMovieTitle());
            overview.setText(mv.getMovieOverview());
            rating.setText(mv.getRating());
            release.setText(mv.getMovieDate());

            Uri posterUri = mv.buildUri(this.getString(R.string.api_poster_default_size));
            Picasso.with(getContext())
                    .load(posterUri)
                    .into(poster);
        }

        mTrailerAdapter = new TrailerAdapter(getActivity());
        mReviewsAdapter = new ReviewsAdapter(getActivity());

        LinearListView llvTrailers = (LinearListView) view.findViewById(R.id.trailerVideoes);
        llvTrailers.setAdapter(mTrailerAdapter);

        llvTrailers.setOnItemClickListener(new LinearListView.OnItemClickListener(){

            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                Trailers t = mTrailerAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + t.getTrailerKey()));
                startActivity(intent);
            }
        });

        LinearListView llvReviews = (LinearListView) view.findViewById(R.id.reviewList);
        llvReviews.setAdapter(mReviewsAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mv != null) {
            int id =mv.id;
            Log.d(LOG_TAG,"MovieID: " +id);
            new FetchMovieTrailers().execute(id);
            new FetchMovieReviews().execute(id);
        }
    }

    public class FetchMovieTrailers extends AsyncTask<Integer, Void, ArrayList<Trailers>>{

        private final String LOG_TAG = FetchMovieTrailers.class.getSimpleName();

        @Override
        protected ArrayList<Trailers> doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String responseJsonStr = null;

            try {
                final String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String Video = "videos";
                final String API_KEY = "api_key";

                if (isAdded()) {
                    Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                            .appendPath(String.valueOf(params[0]))
                            .appendPath(Video)
                            .appendQueryParameter(API_KEY, getString(R.string.api_key))//Enter TMDB API-KEY
                            .build();

                    // /http://api.themoviedb.org/3/movie/209112/videos?api_key=**
                    Log.d(LOG_TAG, "TRAILER URI: " + builtUri.toString());
                    URL url = new URL(builtUri.toString());

                    // Create the request to themoviedb api, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    responseJsonStr = buffer.toString();
                }
                }catch(Exception ex){
                    Log.e(LOG_TAG, "Error", ex);
                    return null;
                }finally{
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
            try {
                return fetchTrailersFromJson(responseJsonStr);
            } catch (JSONException ex) {
                Log.d(LOG_TAG, "Can't parse JSON: " + responseJsonStr, ex);
                return null;
            }
        }

        /*
        Method to extract relevant Movie data
         */
        private ArrayList<Trailers> fetchTrailersFromJson(String jsonStr) throws JSONException {

            JSONObject json  = new JSONObject(jsonStr);
            JSONArray trailerArray = json.getJSONArray("results");
            ArrayList<Trailers> result = new ArrayList<>();

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                if(trailer.getString("site").contentEquals("YouTube")) {
                    Trailers t = new Trailers(trailer);
                    result.add(t);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Trailers> trailers) {

            if(trailers != null) {
               if(trailers.size() >0) {

                   mTrailerAdapter.clear(trailers);
                   mTrailerAdapter.add(trailers);

                   trail = trailers.get(0);
                   if(mShareActionProvider != null){
                       mShareActionProvider.setShareIntent(setIntent());
                   }
               }
            }
        }
    }

    public class FetchMovieReviews extends AsyncTask<Integer, Void, ArrayList<Reviews>> {

        private final String LOG_TAG = FetchMovieReviews.class.getSimpleName();

        @Override
        protected ArrayList<Reviews> doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String responseJsonStr = null;

            try {
                final String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String Movie = "reviews";
                final String API_KEY = "api_key";

                if(isAdded()) {
                    Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                            .appendPath(String.valueOf(params[0]))
                            .appendPath(Movie)
                            .appendQueryParameter(API_KEY, getString(R.string.api_key))//Enter TMDB API-KEY
                            .build();

                    //http://api.themoviedb.org/3/movie/209112/reviews?api_key=**
                    Log.d(LOG_TAG, "REVIEWS URI: " + builtUri.toString());
                    URL url = new URL(builtUri.toString());

                    // Create the request to themoviedb api, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    responseJsonStr = buffer.toString();
                }
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Error", ex);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return fetchReviewsFromJson(responseJsonStr);
            } catch (JSONException ex) {
                Log.d(LOG_TAG, "Can't parse JSON: " + responseJsonStr, ex);
                return null;
            }
        }

        /*
        Method to extract relevant Movie data
         */
        private ArrayList<Reviews> fetchReviewsFromJson(String jsonStr) throws JSONException {

            JSONObject json  = new JSONObject(jsonStr);
            JSONArray reviewArray = json.getJSONArray("results");
            ArrayList<Reviews> result = new ArrayList<>();

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                    result.add(new Reviews(review));
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Reviews> reviews) {
            if(reviews != null) {
                if(reviews.size()>0) {
                        mReviewsAdapter.clear();
                        mReviewsAdapter.add(reviews);
                }
            }
        }
    }

    public void ratingBar(final MenuItem item){
        if( mv != null){
            new AsyncTask<Void,Void,Integer>(){

                @Override
                protected Integer doInBackground(Void... params) {
                    return favoriteMovies(getActivity(),mv.getMovieID());
                }

                @Override
                protected void onPostExecute(Integer integer) {
                    //if the movie is in favorites
                    if(integer == 1){
                        //delete the movie from favorites
                        new AsyncTask<Void,Void,Integer>(){
                            @Override
                            protected Integer doInBackground(Void... params) {
                                return getActivity().getContentResolver().delete(
                                        MoviesContract.MoviesEntry.CONTENT_URI,
                                        MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ?",
                                        new String[]{Integer.toString(mv.getMovieID())}
                                );
                            }

                            @Override
                            protected void onPostExecute(Integer rowsDeleted) {
                                item.setIcon(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                                if(t != null){
                                    t.cancel();
                                }
                                t=Toast.makeText(getActivity(),
                                        "Removed from favorites",
                                        Toast.LENGTH_SHORT);
                                t.show();
                            }
                        }.execute();
                    }
                    //if it is not in favorites, than add it
                    else{
                        new AsyncTask<Void,Void,Uri>(){
                            @Override
                            protected Uri doInBackground(Void... params) {
                                ContentValues cv = new ContentValues();

                                cv.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, mv.getMovieID());
                                cv.put(MoviesContract.MoviesEntry.COLUMN_TITLE, mv.getMovieTitle());
                                cv.put(MoviesContract.MoviesEntry.COLUMN_POSTER, mv.getMoviePoster());
                                cv.put(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS, mv.getMovieOverview());
                                cv.put(MoviesContract.MoviesEntry.COLUMN_RATING, mv.getMovieRating());
                                cv.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, mv.getMovieDate());

                                return getActivity().getContentResolver().insert(
                                        MoviesContract.MoviesEntry.CONTENT_URI,cv);
                            }

                            @Override
                            protected void onPostExecute(Uri uri) {
                                item.setIcon(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                                if(t != null){
                                    t.cancel();
                                }
                                t=Toast.makeText(getActivity(),
                                        "Added to favorites",
                                        Toast.LENGTH_SHORT);
                                t.show();
                            }
                        }.execute();
                    }
                }
            }.execute();
        }
    }

    public int favoriteMovies(Context c, int id) {
        int rows = 0;
        if (DetailsActivityFragment.this.isAdded()) {
            Cursor cursor = c.getContentResolver().query(
                    MoviesContract.MoviesEntry.CONTENT_URI,
                    null, //projection
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ?", //selection
                    new String[]{Integer.toString(id)}, //selection args
                    null //sort order
            );
            rows = cursor.getCount();
            Log.d(LOG_TAG, "Liked movies: " + rows);
            cursor.close();
        }
            return rows;
        }
}
