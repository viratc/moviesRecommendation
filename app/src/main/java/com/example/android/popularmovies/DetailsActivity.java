package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    private final String LOG_TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if(savedInstanceState == null){
            Bundle b = new Bundle();
            b.putParcelable(DetailsActivityFragment.MOVIE_DETAILS,
                    getIntent().getParcelableExtra(DetailsActivityFragment.MOVIE_DETAILS));

            DetailsActivityFragment fragment = new DetailsActivityFragment();
            fragment.setArguments(b);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details,fragment)
                    .commit();
        }

    }

}
