package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by hanapearlman on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context context;

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

        public ViewHolder(View itemView) {
            super(itemView);

            // findViewById lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvRelTime = (TextView) itemView.findViewById(R.id.tvRelTime);
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
