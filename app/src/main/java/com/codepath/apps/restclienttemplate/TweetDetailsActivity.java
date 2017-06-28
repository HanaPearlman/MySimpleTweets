package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        client = TwitterApp.getRestClient();
        TextView userName = (TextView) findViewById(R.id.tvUserName);
        TextView screenName = (TextView) findViewById(R.id.tvScreenName);
        TextView tweetBody = (TextView) findViewById(R.id.tvBody);
        ImageView profileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ImageButton retweet = (ImageButton) findViewById(R.id.ibRetweet);
        ImageButton favorite = (ImageButton) findViewById(R.id.ibFavorite);

        Intent intent = getIntent();
        Tweet tweet = (Tweet) Parcels.unwrap(intent.getParcelableExtra("tweet"));
        userName.setText(tweet.user.name);
        screenName.setText(tweet.user.screenName);
        tweetBody.setText(tweet.body);

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(profileImage);

        retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: add network call to retweet the tweet, Toast success
                //check if tweet is already retweeted
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: add network call to favorite the tweet, Toast success
                //check if tweet is already favorited
            }
        });
    }
}
