package com.ValentineRutto.SearchTwitter.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ValentineRutto.SearchTwitter.R;
import com.ValentineRutto.SearchTwitter.models.TweetDateFormatter;
import com.ValentineRutto.SearchTwitter.models.Tweet;

import java.util.List;

public class TweetListAdapter extends AnimatedRecyclerViewAdapter<TweetListAdapter.ViewHolder> {

    private final TweetDateFormatter mFormatter;
    private List<Tweet> mTweetList;

    public TweetListAdapter(Context context, TweetDateFormatter formatter) {
        super(context);
        this.mFormatter = formatter;
    }

    public void update(List<Tweet> tweetList) {
        this.mTweetList = tweetList;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View tweetItemView = inflater.inflate(R.layout.tweet_item, parent, false);
        return new ViewHolder(tweetItemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
//        super.onBindViewHolder(holder, position);

        Tweet tweet = mTweetList.get(position);
        holder.content.setText(tweet.getContent());
        holder.createdAt.setText(mFormatter.format(mContext, tweet.getCreatedAt()));
        holder.username.setText(tweet.getUsername());
        String imageUrl = tweet.getImageUrl();
        if (TextUtils.isEmpty(imageUrl)) {
            holder.imageView.setVisibility(View.GONE);
        } else {
            Glide.with(mContext).load(imageUrl).centerCrop().into(holder.imageView);
            holder.imageView.setVisibility(View.VISIBLE);
        }

  holder.content.setText(mTweetList.get(position).getContent());
  holder.content.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v) {
          Tweet tweet = mTweetList.get(position);
          WebView wv = new WebView(mContext);
          wv.loadUrl(String.valueOf(tweet));
          AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
          wv.setWebViewClient(new WebViewClient(){
              @Override
              public  boolean shouldOverrideUrlLoading(WebView view, String url){
                  view.loadUrl(url);
                  return true;
              }
          });
          alert.setView(wv);
                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            dialog.dismiss();
        }
    });
          AlertDialog dialog = alert.create();
              dialog.show();
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
    lp.gravity = Gravity.CENTER;
                dialog.getWindow().setAttributes(lp);
    Button positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
    LinearLayout parent = (LinearLayout) positiveButton.getParent();
                parent.setGravity(Gravity.CENTER_HORIZONTAL);
    View leftSpacer = parent.getChildAt(1);
                leftSpacer.setVisibility(View.GONE);


      }


  });

    }
//    wv.setWebViewClient(new WebViewClient() {
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return true;
//        }
//    });
//                alert.setView(wv);
//                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int id) {
//            dialog.dismiss();
//        }
//    });
//    android.app.AlertDialog dialog = alert.create();
//                dialog.show();
//    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//                lp.copyFrom(dialog.getWindow().getAttributes());
//    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//    lp.gravity = Gravity.CENTER;
//                dialog.getWindow().setAttributes(lp);
//    Button positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
//    LinearLayout parent = (LinearLayout) positiveButton.getParent();
//                parent.setGravity(Gravity.CENTER_HORIZONTAL);
//    View leftSpacer = parent.getChildAt(1);
//                leftSpacer.setVisibility(View.GONE);

                @Override
    public int getItemCount() {
//        return mTweetList == null ? 0 : mTweetList.size();
                    return mTweetList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView username;
        private final TextView createdAt;
        private final TextView content;
        private final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.usernameTextView);
            createdAt = (TextView) itemView.findViewById(R.id.createdAtTextView);
            content = (TextView) itemView.findViewById(R.id.contentTextView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
