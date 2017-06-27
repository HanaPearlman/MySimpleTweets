package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by hanapearlman on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private static List<Tweet> mTweets;
    private static Context context;     //IS THIS OK?

    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
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
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvRelTime.setText(getRelativeTimeAgo(tweet.createdAt));
        holder.tvScreenName.setText(tweet.user.screenName);
        holder.tvFavoriteCount.setText("" + tweet.faveCount);
        holder.tvRetweetCount.setText("" + tweet.retweetCount);

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    //create the ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvScreenName;
        public TextView tvRelTime;
        public TextView tvRetweetCount;
        public TextView tvFavoriteCount;
        public ImageButton ibRetweet;
        public ImageButton ibFavorite;
        public ImageButton ibReply;

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
            ibRetweet = (ImageButton) itemView.findViewById(R.id.ibRetweet);
            ibFavorite = (ImageButton) itemView.findViewById(R.id.ibFavorite);
            ibReply = (ImageButton) itemView.findViewById(R.id.ibReply);

            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    // make sure the position is valid
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position
                        Tweet tweet = mTweets.get(position);
                        // create intent for the new activity
                        //Intent intent = new Intent(context, MovieDetailsActivity.class);
                        // serialize the movie using parceler, use its short name as a key
                        //intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                        // show the activity
                        //context.startActivity(intent);

                        //TODO: create new activity for replying to a tweet
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    // make sure the position is valid
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position
                        Tweet tweet = mTweets.get(position);
                        Intent intent = new Intent(context, TweetDetailsActivity.class);
                        intent.putExtra("tweet", Parcels.wrap(tweet));
                        context.startActivity(intent);
                    }
                }
            });


            //TODO: maybe make on-click listeners for profile, username, screen-name, body

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

            // TODO: this is very hacky, please fix
            relativeDate = relativeDate.substring(0, relativeDate.length() - 4);
            relativeDate = relativeDate.replace(" seconds", "s");
            relativeDate = relativeDate.replace(" second", "s");
            relativeDate = relativeDate.replace(" minutes", "m");
            relativeDate = relativeDate.replace(" minute", "m");
            relativeDate = relativeDate.replace(" hours", "h");
            relativeDate = relativeDate.replace(" hour", "h");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
