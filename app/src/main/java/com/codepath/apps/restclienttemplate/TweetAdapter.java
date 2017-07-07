package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by hanapearlman on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context context;
    private TweetAdapterListener mListener;

    //define an interface required by the ViewHolder
    public interface TweetAdapterListener {
        public void onItemSelected(View view, int position);
        public void onProfileSelected(View view, int position);
    }

    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets, TweetAdapterListener listener) {
        mTweets = tweets;
        mListener = listener;
    }

    //for each row, inflate the layout and cache references into Viewholder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_placeholder)
                //.bitmapTransform(new CropCircleTransformation(context))
                .bitmapTransform(new RoundedCornersTransformation(context, 10, 0))
                .into(holder.ivProfileImage);

        if (tweet.includesMedia) {
            Glide.with(context)
                    .load(tweet.mediaUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(holder.ivMedia);
            holder.ivMedia.setVisibility(View.VISIBLE);
        } else {
            holder.ivMedia.setVisibility(View.GONE);
        }

        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvRelTime.setText(getRelativeTimeAgo(tweet.createdAt));
        holder.tvScreenName.setText(tweet.user.screenName);

        holder.tvFavoriteCount.setText("" + tweet.faveCount);
        if (tweet.favorited) {
            holder.ibFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.ibFavorite.setImageResource(R.drawable.ic_favorite_stroke);
        }

        holder.tvRetweetCount.setText("" + tweet.retweetCount);
        if (tweet.retweeted) {
            holder.ibRetweet.setImageResource(R.drawable.ic_retweet);
        } else {
            holder.ibRetweet.setImageResource(R.drawable.ic_retweet_stroke);
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    //create the ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvScreenName;
        public TextView tvRelTime;
        public TextView tvRetweetCount;
        public TextView tvFavoriteCount;
        public ImageView ibRetweet;
        public ImageView ibFavorite;
        public ImageView ibReply;
        public ImageView ivMedia;

        public ViewHolder(View itemView) {
            super(itemView);

            // findViewById lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvRelTime = (TextView) itemView.findViewById(R.id.tvRelTime);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvFavoriteCount = (TextView) itemView.findViewById(R.id.tvFavoriteCount);
            ibRetweet = (ImageView) itemView.findViewById(R.id.ibRetweet);
            ibFavorite = (ImageView) itemView.findViewById(R.id.ibFavorite);
            ibReply = (ImageView) itemView.findViewById(R.id.ibReply);
            ivMedia = (ImageView) itemView.findViewById(R.id.ivMedia);

            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    // make sure the position is valid
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position
                        Tweet tweet = mTweets.get(position);
                        String replyUser = tweet.user.screenName;
                        long id = tweet.uid;
                        showAlertDialogForCompose(context, replyUser, id);
                    }
                }
            });

            tvBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        // make sure the position is valid
                        if (position != RecyclerView.NO_POSITION) {
                            // get the tweet at the position
                            mListener.onItemSelected(view, position);
                        }
                    }
                }
            });

            ivMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        // make sure the position is valid
                        if (position != RecyclerView.NO_POSITION) {
                            // get the tweet at the position
                            mListener.onItemSelected(view, position);
                        }
                    }
                }
            });

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        // make sure the position is valid
                        if (position != RecyclerView.NO_POSITION) {
                            // get the tweet at the position
                            mListener.onProfileSelected(view, position);
                        }
                    }
                }
            });
        }
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

            relativeDate = relativeDate.replace(" seconds", "s");
            relativeDate = relativeDate.replace(" second", "s");
            relativeDate = relativeDate.replace(" minutes", "m");
            relativeDate = relativeDate.replace(" minute", "m");
            relativeDate = relativeDate.replace(" hours", "h");
            relativeDate = relativeDate.replace(" hour", "h");
            relativeDate = relativeDate.replace(" ago", "");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    private static void showAlertDialogForCompose(Context context1, String originalUser, long replyID) {

        final Context context = context1;
        final long inReplyToStatusId = replyID;
        final String replyUser = originalUser;

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
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Tweet",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String reply = etName.getText().toString();
                        reply = replyUser + " " + reply;
                        client.reply(reply, inReplyToStatusId, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Toast.makeText(context, "Reply sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, TimeLineActivity.class);
                                context.startActivity(intent);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(context, "Reply failed", Toast.LENGTH_SHORT).show();
                                Log.e("ComposeTweet onFailure", "Failure replying", error);
                            }
/*
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Toast.makeText(context, "Reply sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, TimeLineActivity.class);
                                context.startActivity(intent);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toast.makeText(context, "Reply failed", Toast.LENGTH_SHORT).show();
                                Log.e("ComposeTweet onFailure", "Failure replying", throwable);
                            }*/

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
