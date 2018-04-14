package com.example.android.popularmovies.DataClass;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by virat on 15/04/16.
 */
public class Reviews {

    private String id="id";
    private String author= "author";
    private String content="content";

    public Reviews(){}

    public Reviews(JSONObject review) throws JSONException{
        this.id=review.getString(id);
        this.author = review.getString(author);
        this.content = review.getString(content);
    }

    public String getReviewsID(){return id;}

    public String getReviewsAuthor(){return author;}

    public String getReviewsContent(){return content;}
}
