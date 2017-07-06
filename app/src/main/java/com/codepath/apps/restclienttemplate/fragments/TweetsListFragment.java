package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hanapearlman on 7/3/17.
 */

public class TweetsListFragment extends Fragment implements TweetAdapter.TweetAdapterListener {

    public interface TweetSelectedListener {
        public void onTweetSelected(Tweet tweet);
    }

    public interface  UserSelectedListener {
        public void onUserSelected(User user);
    }

    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;

    //inflation happens in onCreateView

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout
        View v = inflater.inflate(R.layout.fragments_tweet_list, container, false);

        // find the RecyclerView
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweet);
        // init the arrayList (data source)
        tweets = new ArrayList<>();
        // construct adapter from source
        tweetAdapter = new TweetAdapter(tweets, this);
        // RecyclerView setup (layout manager, adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTweets.setAdapter(tweetAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(dividerItemDecoration);

        return v;
    }

    public void addItems(JSONArray response) {
        try {
            for (int i = 0; i < response.length(); i++) {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweets.add(tweet);
                tweetAdapter.notifyItemInserted(i - 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addItem(JSONObject response) {
        try {
            Tweet tweet = Tweet.fromJSON(response);
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.getLayoutManager().scrollToPosition(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(View view, int position) {
        Tweet tweet = tweets.get(position);
        ((TweetSelectedListener) getActivity()).onTweetSelected(tweet);
    }

    @Override
    public void onProfileSelected(View view, int position) {
        Tweet tweet = tweets.get(position);
        User user = tweet.user;
        ((UserSelectedListener) getActivity()).onUserSelected(user);
    }

    public void onCompose() {

    }
}
