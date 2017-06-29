package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class TweetDetailsActivity extends AppCompatActivity {

    TwitterClient client; //should I get rid of this? and add context?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        Intent intent = getIntent();
        final Tweet tweet = (Tweet) Parcels.unwrap(intent.getParcelableExtra("tweet"));

        client = TwitterApp.getRestClient();
        TextView userName = (TextView) findViewById(R.id.tvUserName);
        TextView screenName = (TextView) findViewById(R.id.tvScreenName);
        TextView tweetBody = (TextView) findViewById(R.id.tvBody);
        ImageView profileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ImageView retweet = (ImageView) findViewById(R.id.ibRetweet);
        ImageView reply = (ImageView) findViewById(R.id.ivReply);

        if (tweet.retweeted) {
            retweet.setImageResource(R.drawable.ic_retweet);
        } else {
            retweet.setImageResource(R.drawable.ic_retweet_stroke);
        }

        ImageView favorite = (ImageView) findViewById(R.id.ibFavorite);
        if (tweet.favorited) {
            favorite.setImageResource(R.drawable.ic_favorite);
        } else {
            favorite.setImageResource(R.drawable.ic_favorite_stroke);
        }

        userName.setText(tweet.user.name);
        screenName.setText(tweet.user.screenName);
        tweetBody.setText(tweet.body);
        final long id = tweet.uid;

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(profileImage);

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ComposeTweetActivity.class);
                intent.putExtra("replying", true);
                intent.putExtra("replyUser", tweet.user.screenName);
                intent.putExtra("inReplyToStatusId", tweet.uid);
                getApplicationContext().startActivity(intent);
            }
        });

        retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwitterClient client = TwitterApp.getRestClient();
                client.retweet(id, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        //mark tweet as retweeted?
                        tweet.retweeted = true;
                        Toast.makeText(getApplicationContext(), "Retweeted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), TimeLineActivity.class);
                        getApplicationContext().startActivity(intent);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(getApplicationContext(), "Failed to Retweet", Toast.LENGTH_SHORT).show();
                        Log.e("TweetDetails", "Failed to retweet", throwable);
                    }
                });
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: add network call to favorite the tweet, Toast success
                TwitterClient client = TwitterApp.getRestClient();
                if (tweet.favorited) {
                    client.unfavorite(id, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            tweet.favorited = false;
                            Toast.makeText(getApplicationContext(), "Unfavorited", Toast.LENGTH_SHORT).show(); //might want to have this return an intent? so it doesn't scroll back to the top?
                            Intent intent = new Intent(getApplicationContext(), TimeLineActivity.class);
                            getApplicationContext().startActivity(intent);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toast.makeText(getApplicationContext(), "Failed to un-favorite", Toast.LENGTH_SHORT).show();
                            Log.e("TweetDetails", "Failed to un-favorite", throwable);
                        }
                    });
                } else {
                    client.favorite(id, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            tweet.favorited = true;
                            //mark tweet as faved?
                            Toast.makeText(getApplicationContext(), "Favorited", Toast.LENGTH_SHORT).show(); //might want to have this return an intent? so it doesn't scroll back to the top?
                            Intent intent = new Intent(getApplicationContext(), TimeLineActivity.class);
                            getApplicationContext().startActivity(intent);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toast.makeText(getApplicationContext(), "Failed to Favorite", Toast.LENGTH_SHORT).show();
                            Log.e("TweetDetails", "Failed to favorite", throwable);
                        }
                    });
                }

            }
        });
    }
}
