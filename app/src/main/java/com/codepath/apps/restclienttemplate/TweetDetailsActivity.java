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
    import android.view.Menu;
    import android.view.MenuItem;
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
    import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

    public class TweetDetailsActivity extends AppCompatActivity {

    TwitterClient client;
    Context context;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_tweet_details);
        Intent intent = getIntent();
        tweet = (Tweet) Parcels.unwrap(intent.getParcelableExtra("tweet"));

        client = TwitterApp.getRestClient();
        TextView userName = (TextView) findViewById(R.id.tvUserName);
        TextView screenName = (TextView) findViewById(R.id.tvScreenName);
        TextView tweetBody = (TextView) findViewById(R.id.tvBody);
        TextView tvFaveCount = (TextView) findViewById(R.id.tvFaveCount);
        TextView tvRetweetCount = (TextView) findViewById(R.id.tvRetweetCount);
        ImageView profileImage = (ImageView) findViewById(R.id.ivProfileImage);

        ImageView retweet = (ImageView) findViewById(R.id.ibRetweet);
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

        ImageView media = (ImageView) findViewById(R.id.ivMedia);
        if(tweet.includesMedia) {
            Glide.with(this)
                    .load(tweet.mediaUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(media);
        } else {
            media.setVisibility(View.GONE);
        }

        userName.setText(tweet.user.name);
        screenName.setText(tweet.user.screenName);
        tweetBody.setText(tweet.body);
        tvFaveCount.setText("" + tweet.faveCount);
        tvRetweetCount.setText("" + tweet.retweetCount);

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .bitmapTransform(new RoundedCornersTransformation(context, 10, 0))
                .into(profileImage);

        setReplyOnClickListener();
        setFavoriteOnClickListener();
        setRetweetOnClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_timeline, menu);
        return true;
    }

    public void setReplyOnClickListener() {
        ImageView reply = (ImageView) findViewById(R.id.ivReply);
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
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Tweet",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String reply = etName.getText().toString();
                                reply = replyUser + " " + reply;
                                client.reply(reply, inReplyToStatusId, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Toast.makeText(context, "Reply sent", Toast.LENGTH_SHORT).show();
                                        //Intent intent = new Intent(context, TimeLineActivity.class);
                                        //context.startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        Toast.makeText(context, "Reply failed", Toast.LENGTH_SHORT).show();
                                        Log.e("ComposeTweet onFailure", "Failure replying", throwable);
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
        });
    }

    public void setFavoriteOnClickListener() {
        ImageView favorite = (ImageView) findViewById(R.id.ibFavorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.favorite(tweet.favorited, tweet.uid, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        tweet.favorited = !tweet.favorited;
                        if (tweet.favorited) {
                            Toast.makeText(context, "Favorited", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Unfavorited", Toast.LENGTH_SHORT).show();
                        }

                        Intent intent = new Intent(context, TweetDetailsActivity.class);
                        intent.putExtra("tweet", Parcels.wrap(tweet));
                        context.startActivity(intent);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(context, "Favorite failed", Toast.LENGTH_SHORT).show();
                        Log.e("TweetDetails", "Failed to un-favorite", throwable);
                    }
                });
            }
        });
    }

    public void setRetweetOnClickListener() {
        ImageView retweet = (ImageView) findViewById(R.id.ibRetweet);
        retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.retweet(tweet.uid, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        //mark tweet as retweeted?
                        tweet.retweeted = true;
                        Toast.makeText(context, "Retweeted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, TweetDetailsActivity.class);
                        intent.putExtra("tweet", Parcels.wrap(tweet));
                        context.startActivity(intent);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(context, "Failed to Retweet", Toast.LENGTH_SHORT).show();
                        Log.e("TweetDetails", "Failed to retweet", throwable);
                    }
                });
            }
        });
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
