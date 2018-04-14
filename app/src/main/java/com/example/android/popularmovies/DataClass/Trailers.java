package com.example.android.popularmovies.DataClass;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by virat on 15/04/16.
 */
public class Trailers {

    private String id = "id";
    private String key = "key";
    private String name = "name";
    private String site = "site";
    private String type = "type";

    public Trailers(){}

    public Trailers(JSONObject trailers) throws JSONException{
        this.id = trailers.getString(id);
        this.key = trailers.getString(key);
        this.name = trailers.getString(name);
        this.site = trailers.getString(site);
        this.type = trailers.getString(type);
    }

    public String getTrailerId(){return id;}

    public String getTrailerKey(){return key;}

    public String getTrailerName(){return name;}

    public String getTrailerSite(){return site;}

    public String getTrailerType(){return type;}
}
