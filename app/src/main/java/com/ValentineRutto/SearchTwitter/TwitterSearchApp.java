package com.ValentineRutto.SearchTwitter;

import android.app.Application;

import com.ValentineRutto.SearchTwitter.repository.TwitterRepository;

public class TwitterSearchApp extends Application {

    private TwitterRepository mTwitterRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        mTwitterRepository = new TwitterRepository(this,
                getString(R.string.base_url),
                getString(R.string.consumer_key),
         getString(R.string.consumer_secret));
    }

    public TwitterRepository getRepository() {
        return mTwitterRepository;
    }
}
