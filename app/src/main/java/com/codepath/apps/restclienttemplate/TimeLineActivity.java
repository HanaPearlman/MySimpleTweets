package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class TimeLineActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener, TweetsListFragment.UserSelectedListener {
    private final int REQUEST_CODE = 20;
    //Context context;
    TweetsPagerAdapter pagerAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //context = this;
        setContentView(R.layout.activity_timeline);
        pagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());

        //get the view pager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);

        //set the adapter for the pager
        vpPager.setAdapter(pagerAdapter);

        //setup the tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);}

    public void onComposeAction(MenuItem mi) {
        final Context context = this;
        View messageView = LayoutInflater.from(this).
                inflate(R.layout.compose_modal, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(messageView);

        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        final EditText etName = (EditText) messageView.findViewById(R.id.etTweet);
        final TwitterClient client = TwitterApp.getRestClient();
        final TextView tvCharCount = (TextView) messageView.findViewById(R.id.tvCharCount);

        final TextView tvReplyTo = (TextView) messageView.findViewById(R.id.tvReplyTo);
        tvReplyTo.setVisibility(View.GONE);


        TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCount.setText(String.valueOf(140 - s.length()));
            }

            public void afterTextChanged(Editable s) {}
        };
        etName.addTextChangedListener(mTextEditorWatcher);

        // Configure dialog button (OK)
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Tweet",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        client.sendTweet(etName.getText().toString(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Toast.makeText(context, "Tweet sent", Toast.LENGTH_SHORT).show();
                                ((TweetsListFragment) pagerAdapter.getItem(0)).addItem(response);
                                //Intent intent = new Intent(context, TimeLineActivity.class);
                                //context.startActivity(intent);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toast.makeText(context, "Tweet failed", Toast.LENGTH_SHORT).show();
                                Log.e("ComposeTweet onFailure", "Failure tweeting", throwable);
                            }
                        });
                    }
                });

        ImageView ivCancel = (ImageView) messageView.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        // Display the dialog
        alertDialog.show();
    }

    public void onProfileView(MenuItem item) {
        //launch profile view
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }*/

    @Override
    public void onTweetSelected(Tweet tweet) {
        Intent intent = new Intent(this, TweetDetailsActivity.class);
        intent.putExtra("tweet", Parcels.wrap(tweet));
        startActivity(intent);
    }

    @Override
    public void onUserSelected(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("screen_name", user.screenName);
        startActivity(intent);
    }
}
