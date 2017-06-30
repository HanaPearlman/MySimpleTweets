package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by hanapearlman on 6/26/17.
 */

@Parcel
public class Tweet {
    // tweet attributes
    public String body;
    public long uid; //database id
    public User user;
    public String createdAt;
    public int faveCount;
    public int retweetCount;
    public boolean favorited;
    public boolean retweeted;
    public boolean includesMedia;
    public String mediaUrl;

    public Tweet() {

    }
    // deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.faveCount = jsonObject.getInt("favorite_count");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");


        JSONObject entities = jsonObject.getJSONObject("entities");
        if(entities.has("media")) {
            tweet.includesMedia = entities.getJSONArray("media").length() > 0;
        }

        if (tweet.includesMedia) {
            tweet.mediaUrl = entities.getJSONArray("media").getJSONObject(0).getString("media_url");
            tweet.mediaUrl += ":small";
        } else {
            tweet.mediaUrl = "";
        }

        return tweet;
    }
}
