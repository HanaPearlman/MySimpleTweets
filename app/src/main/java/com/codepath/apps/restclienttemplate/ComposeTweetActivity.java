package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeTweetActivity extends AppCompatActivity {

    boolean replying;
    long inReplyToStatusId;
    String replyUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compose_tweet);
        TextView tvReplyTo = (TextView) findViewById(R.id.tvReplyTo);
        Intent intent = getIntent();
        replying = intent.getBooleanExtra("replying", false);
        final TextView tvCharCount = (TextView) findViewById(R.id.tvCharCount);
        if (replying) {
            replyUser = intent.getStringExtra("replyUser"); //TODO: this is not necessarily the "original" tweeter
            inReplyToStatusId = intent.getLongExtra("in_reply_to_status_id", 0);
            tvReplyTo.setText("Replying to " + replyUser);
            tvCharCount.setText(String.valueOf(140 - replyUser.length()));
        } else {
            tvReplyTo.setVisibility(View.GONE);
        }

        EditText etName = (EditText) findViewById(R.id.etTweet);

        TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                if (replying) {
                    tvCharCount.setText(String.valueOf(140 - s.length() - replyUser.length()));
                } else {
                    tvCharCount.setText(String.valueOf(140 - s.length()));
                }
            }

            public void afterTextChanged(Editable s) {
            }
        };
        etName.addTextChangedListener(mTextEditorWatcher);
    }

    public void onCancel(View v) {
        finish();
    }

    public void onSubmit(View v) {
        EditText etName = (EditText) findViewById(R.id.etTweet);
        TwitterClient client = new TwitterClient(this);
        if (replying) {
            String reply = etName.getText().toString();
            reply = replyUser + " " + reply; //TODO: figure out if it's a problem, now reply must be shorter
            client.reply(reply, inReplyToStatusId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Intent intent = new Intent(getApplicationContext(), TimeLineActivity.class);
                    getApplicationContext().startActivity(intent);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e("ComposeTweet onFailure", "Failure replying", throwable);
                }
            });
        } else {
            client.sendTweet(etName.getText().toString(), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        Log.i("ComposeTweetActivity", "onSuccess called");
                        Tweet tweet = Tweet.fromJSON(response);
                        Intent intent = new Intent();
                        intent.putExtra("tweet", Parcels.wrap(tweet));
                        intent.putExtra("code", 200); // ints work too
                        // Activity finished ok, return the data
                        setResult(RESULT_OK, intent); // set result code and bundle data for response
                        finish(); // closes the activity, pass data to parent
                    } catch (JSONException e) {
                        Log.e("ComposeTweetActivity", "onSuccess called, failed", e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e("ComposeTweet onFailure", "Failure submitting tweet", throwable);
                }
            });
        }
    }
}
