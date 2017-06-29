package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimeLineActivity extends AppCompatActivity {

    TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    private final int REQUEST_CODE = 20;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        client = TwitterApp.getRestClient();

        // find the RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        // init the arrayList (data source)
        tweets = new ArrayList<>();
        // construct adapter from source
        tweetAdapter = new TweetAdapter(tweets);
        // RecyclerView setup (layout manager, adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(tweetAdapter);
        populateTimeline();
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Remember to CLEAR OUT old items before appending in the new ones
                tweetAdapter.clear();
                // ...the data has come back, add new items to your adapter...
                //tweetAdapter.addAll(tweets);
                populateTimeline();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
            }
        });
    }


    public void onComposeAction(MenuItem mi) {
        // first parameter is the context, second is the class of the activity to launch
        Intent i = new Intent(TimeLineActivity.this, ComposeTweetActivity.class);
        i.putExtra("replying", false);
        // put "extras" into the bundle for access in the edit activity
        //i.putExtra(ITEM_TEXT, items.get(position));
        //i.putExtra(ITEM_POSITION, position);
        // brings up the edit activity with the expectation of a result
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
            //Toast.makeText(this, "Tweet successful", Toast.LENGTH_SHORT).show();
            // Toast the name to display temporarily on screen
            // TODO: toast removed but could do a notification at the bottom
        }
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Log.d("TwitterClient", response.toString());
                // iterate through JSON
                // deserialize each object
                for (int i = 0; i < response.length(); i++) {
                    //convert each to Tweet model
                    //add tweet model to data source
                    //notify adapter of change
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(i - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }
}
