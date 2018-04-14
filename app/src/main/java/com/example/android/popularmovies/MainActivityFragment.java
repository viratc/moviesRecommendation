package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.Adapters.ImageAdapter;
import com.example.android.popularmovies.Data.MoviesContract;
import com.example.android.popularmovies.Data.MoviesDb;
import com.example.android.popularmovies.DataClass.SampleMovie;

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
import java.util.Collection;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public  final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private GridView gridview;
    private ImageAdapter mImages;
    private ProgressBar mProgressBar;
    private String mKey = "movies";
    private String mSORTKEY = "sort_setting";
    private String HIGHRATED = "top_rated";
    private String POPULAR = "popular";
    private String FAVORTIE = "favorite";
    private String UPCOMING = "upcoming";
    private String RECOMMENDED = "recommendations";
    private String SORTBY = HIGHRATED;

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.COLUMN_POSTER,
            MoviesContract.MoviesEntry.COLUMN_SYNOPSIS,
            MoviesContract.MoviesEntry.COLUMN_RATING,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE
    };

    public static final int COLUMN_ID=0;
    public static final int COLUMN_MOVIE_ID =1;
    public static final int COLUMN_TITLE=2;
    public static final int COLUMN_POSTER=3;
    public static final int COLUMN_SYNOPSIS=4;
    public static final int COLUMN_RATING=5;
    public static final int COLUMN_RELEASEDATE=6;

    public MainActivityFragment() {
    }

    public interface Callback{
        void onItemSelected(SampleMovie mv);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_frament_main, menu);

        MenuItem highestRated = menu.findItem(R.id.rated);
        //MenuItem popular = menu.findItem(R.id.popular);
        MenuItem favorite = menu.findItem(R.id.favorites);
        //MenuItem upcoming = menu.findItem(R.id.upcoming);
        MenuItem recommended = menu.findItem(R.id.recommended);

        if (SORTBY.contentEquals(HIGHRATED)) {
            if (!highestRated.isChecked()) {
                highestRated.setChecked(true);
            }
        }/*else if (SORTBY.contentEquals(POPULAR)){
            if(!popular.isChecked()){
                popular.setChecked(true);
            }
        }*/else if(SORTBY.contentEquals(FAVORTIE)){
            if(!favorite.isChecked()){
                favorite.setChecked(true);
            }
        }/*else if(SORTBY.contentEquals(UPCOMING)){
            if(!upcoming.isChecked()){
                upcoming.setChecked(true);
            }
        }*/else if(SORTBY.contentEquals(RECOMMENDED)){
            if(!recommended.isChecked()){
                recommended.setChecked(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.rated:
                if(item.isChecked()){
                    item.setChecked(false);
                }else{item.setChecked(true);}
                SORTBY = HIGHRATED;
                startPostersLoading(SORTBY);
                return true;

            /*case R.id.popular:
                if(item.isChecked()){
                    item.setChecked(false);
                }else{item.setChecked(true);}
                SORTBY = POPULAR;
                startPostersLoading(SORTBY);
                return true;*/

            case R.id.favorites:
                if(item.isChecked()){
                    item.setChecked(false);
                }else{item.setChecked(true);}
                SORTBY = FAVORTIE;
                startPostersLoading(SORTBY);
                return true;

            /*case R.id.upcoming:
                if(item.isChecked()){
                    item.setChecked(false);
                }else{item.setChecked(true);}
                SORTBY = UPCOMING;
                startPostersLoading(SORTBY);
                return true;*/

            case R.id.recommended:
                if(item.isChecked()){
                    item.setChecked(false);
                }else{item.setChecked(true);}
                SORTBY = RECOMMENDED;
                startPostersLoading(SORTBY);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mImages = new ImageAdapter(getActivity());
        mProgressBar= (ProgressBar) view.findViewById(R.id.progress);

        gridview = (GridView) view.findViewById(R.id.grid_view);
        gridview.setAdapter(mImages);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SampleMovie movie = mImages.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });

        //This restores data on configuration change using parcelables
        if (savedInstanceState != null) {
            if(savedInstanceState.containsKey(mSORTKEY)){
                SORTBY = savedInstanceState.getString(mSORTKEY);
                Log.d(LOG_TAG, "SORT_BY_SAVED: " +SORTBY);
            }

            if(savedInstanceState.containsKey(mKey)) {
                mImages.mMovies = savedInstanceState.getParcelableArrayList(mKey);
            }else{
                startPostersLoading(SORTBY);
            }
        }else {
            startPostersLoading(SORTBY);
        }
        return view;
    }

    private void startPostersLoading(String s) {
        if (!s.contentEquals(FAVORTIE)) {
            new FetchPageTask().execute(s);
        }else{
            new FetchFavoriteMovies(getContext()).execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(!SORTBY.contentEquals(POPULAR)){
            outState.putString(mSORTKEY,SORTBY);
        }
        if(mImages.mMovies != null) {
            outState.putParcelableArrayList(mKey, mImages.mMovies);
        }
        super.onSaveInstanceState(outState);
    }

    private class FetchPageTask extends AsyncTask<String, Integer, Collection<SampleMovie>> {

        public final String LOG_TAG1 = FetchPageTask.class.getSimpleName();

        //Method to make Progress Bar visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }

        //Code snippet taken from Sunshine (and altered as per the requirement)
        @Override
        protected Collection<SampleMovie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            int page = 1;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String responseJsonStr = null;

            try {
                final String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_PARAM_PAGE = "page";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(API_PARAM_PAGE, String.valueOf(page))
                        .appendQueryParameter(API_KEY, getString(R.string.api_key))//Enter TMDB API-KEY
                        .build();

                //http://api.themoviedb.org/3/movie/top_rated?page=1&api_key=**
                //https://api.themoviedb.org/3/movie/{movie-id}/recommendations?api_key=**
                Log.d(LOG_TAG, "QUERY URI: " + builtUri.toString());
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
                return fetchMoviesFromJson(responseJsonStr);
            } catch (JSONException ex) {
                Log.d(LOG_TAG, "Can't parse JSON: " + responseJsonStr, ex);
                return null;
            }
        }

        /*
        Method to extract relevant Movie data
         */
        private Collection<SampleMovie> fetchMoviesFromJson(String jsonStr) throws JSONException {
            final String KEY_MOVIES = "results";

            JSONObject json  = new JSONObject(jsonStr);
            JSONArray movies = json.getJSONArray(KEY_MOVIES);
            ArrayList<SampleMovie> result = new ArrayList<>();

            for (int i = 0; i < movies.length(); i++) {
                JSONObject jo = movies.getJSONObject(i);
                SampleMovie mv = new SampleMovie(jo);
                result.add(mv);
            }

            return result;
        }

        //Method to publish progress update on UI thread
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressBar.setProgress(values[0]);
        }

        //Method to process the data from background thread to main UI thread
        @Override
        protected void onPostExecute(Collection<SampleMovie> xs) {

            if(xs != null) {
                if(gridview != null){
                    gridview.setAdapter(mImages);
                }
                stopPostersLoading();
                mImages.clearGridView();
                mImages.addAll(xs);
            }else{
                Toast.makeText(
                        getActivity(),
                        getString(R.string.msg_server_error),
                        Toast.LENGTH_SHORT
                ).show();

                stopPostersLoading();
            }
        }
    }

    private void stopPostersLoading() {
        mProgressBar.setVisibility(ProgressBar.GONE);
    }

    //Fetches Favorites Movies
    public class FetchFavoriteMovies extends AsyncTask<Void, Integer, Collection<SampleMovie>>{

        private Context context;

        public FetchFavoriteMovies(Context c){
            context= c;
        }

        private Collection<SampleMovie> getFavoriteMovies(Cursor c){
            ArrayList<SampleMovie> movies= new ArrayList<>();
            if(c != null && c.moveToFirst()){
               do {
                   SampleMovie m = new SampleMovie(c);
                   movies.add(m);
               }while(c.moveToNext());
                c.close();
            }
            return movies;
        }

        @Override
        protected Collection<SampleMovie> doInBackground(Void...params) {
            Cursor cursor = context.getContentResolver().query(
                    MoviesContract.MoviesEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
            return getFavoriteMovies(cursor);
            MoviesDb db = db.getReadableDatabase().query()
        }

        @Override
        protected void onPostExecute(Collection<SampleMovie> sampleMovies) {
            if (sampleMovies != null) {
                mImages.clearGridView();
                mImages.addAll(sampleMovies);
            }
        }
    }

}
