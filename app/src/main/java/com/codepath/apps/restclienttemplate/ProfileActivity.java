package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener {

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String screenName = getIntent().getStringExtra("screen_name");
        //create user fragment
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
        //display the user timeline fragment (dynamically)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        //make change
        ft.replace(R.id.flContainer, userTimelineFragment);
        //commit
        ft.commit();

        client = TwitterApp.getRestClient();
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //deserialize the User object
                    User user = User.fromJSON(response);
                    //set the title of the Action Bar based on user info
                    getSupportActionBar().setTitle(user.screenName);
                    //populate the user headline
                    populateUserHeadline(user);

                } catch (JSONException e){
                    Log.e("ProfileActivity", "Error parsing screen name", e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_timeline, menu);
        return true;
    }

    public void populateUserHeadline(User user) {
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);

        tvName.setText(user.name);
        tvTagline.setText(user.tagLine);
        tvFollowers.setText(user.followersCount + " Followers");
        tvFollowing.setText(user.followingCount + " Following");

        Glide.with(this)
                .load(user.profileImageUrl)
                .placeholder(R.drawable.ic_placeholder)
                //.bitmapTransform(new CropCircleTransformation(context))
                .bitmapTransform(new RoundedCornersTransformation(this, 10, 0))
                .into(ivProfileImage);

    }

    @Override
    public void onTweetSelected(Tweet tweet) {
        Intent intent = new Intent(this, TweetDetailsActivity.class);
        intent.putExtra("tweet", Parcels.wrap(tweet));
        startActivity(intent);
    }

    public void onHomeTimeline(MenuItem item) {
        //launch profile view
        Intent intent = new Intent(this, TimeLineActivity.class);
        startActivity(intent);
    }

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
                                Intent intent = new Intent(context, TimeLineActivity.class);
                                context.startActivity(intent);
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
}
