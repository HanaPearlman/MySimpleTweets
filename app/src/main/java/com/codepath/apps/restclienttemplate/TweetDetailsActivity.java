package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
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

                final long inReplyToStatusId = tweet.uid;
                final String replyUser = tweet.user.screenName;

                // inflate message_item.xml view
                View  messageView = LayoutInflater.from(context).
                        inflate(R.layout.compose_modal, null);
                // Create alert dialog builder
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // set message_item.xml to AlertDialog builder
                alertDialogBuilder.setView(messageView);

                // Create alert dialog

                final AlertDialog alertDialog = alertDialogBuilder.create();
                final EditText etName = (EditText) messageView.findViewById(R.id.etTweet);
                final TwitterClient client = TwitterApp.getRestClient();
                final TextView tvReplyTo = (TextView) messageView.findViewById(R.id.tvReplyTo);
                final TextView tvCharCount = (TextView) messageView.findViewById(R.id.tvCharCount);

                tvReplyTo.setText("Replying to " + replyUser);
                tvCharCount.setText(String.valueOf(140 - replyUser.length()));

                TextWatcher mTextEditorWatcher = new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        tvCharCount.setText(String.valueOf(140 - s.length() - replyUser.length()));
                    }

                    public void afterTextChanged(Editable s) {
                    }
                };
                etName.addTextChangedListener(mTextEditorWatcher);

                // Configure dialog button (OK)
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String reply = etName.getText().toString();
                                reply = replyUser + " " + reply; //TODO: figure out if it's a problem, now reply must be shorter
                                client.reply(reply, inReplyToStatusId, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Intent intent = new Intent(context, TimeLineActivity.class);
                                        context.startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        Log.e("ComposeTweet onFailure", "Failure replying", throwable);
                                    }
                                });
                            }
                        });

                // Configure dialog button (Cancel)
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
                        });

                // Display the dialog
                alertDialog.show();
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
