package com.ValentineRutto.SearchTwitter.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.ValentineRutto.SearchTwitter.models.Tweet;
import com.ValentineRutto.SearchTwitter.network.DateDeserializer;
import com.ValentineRutto.SearchTwitter.network.TwitterApi;
import com.ValentineRutto.SearchTwitter.models.Oauth2Token;
import com.ValentineRutto.SearchTwitter.models.TweetStatuses;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TwitterRepository {

    private static final String SP_TWITTER_REPOSITORY = "SP_TWITTER_REPOSITORY";
    private static final String SP_KEY_ACCESS_TOKEN = "SP_KEY_ACCESS_TOKEN";

    private final TwitterApi mTwitterApi;
    private final SharedPreferences mSharedPreferences;
    private final String mConsumerKey;
    private final String mConsumerSecret;

    private Call<TweetStatuses> mStatusesCall;
    private Call<Oauth2Token> mTokenCall;

    public TwitterRepository(Context context, String baseUrl, String consumerKey, String consumerSecret) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mTwitterApi = retrofit.create(TwitterApi.class);

        mSharedPreferences = context.getSharedPreferences(SP_TWITTER_REPOSITORY, Context.MODE_PRIVATE);

        mConsumerKey = consumerKey;
        mConsumerSecret = consumerSecret;
    }

    public void getTweetList(String query, RepositoryCallback<List<Tweet>> callback) {
        getTweetList(query, callback, 3);
    }

    private void getTweetList(String query, RepositoryCallback<List<Tweet>> callback, int retriesLeft) {
        String accessToken = getAccessToken();
        if (TextUtils.isEmpty(accessToken)) {
            requestAccessTokenAndGetTweetList(query, callback, retriesLeft);
        } else {
            doGetTweetList(query, callback, retriesLeft);
        }
    }

    private void requestAccessTokenAndGetTweetList(final String query,
                                                   final RepositoryCallback<List<Tweet>> callback,
                                                   final int retriesLeft) {
        requestAccessToken(new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void object) {
                doGetTweetList(query, callback, retriesLeft);
            }

            @Override
            public void onFailure(Throwable error) {
                if (callback != null) {
                    callback.onFailure(new RuntimeException("Failed to authenticate"));
                }
            }
        });
    }

    private void requestAccessToken(final RepositoryCallback<Void> callback) {
        safeStop(mTokenCall);
        mTokenCall = mTwitterApi.getAccessToken(getAuthorizationHeader(), "client_credentials");
        mTokenCall.enqueue(new Callback<Oauth2Token>() {
            @Override
            public void onResponse(Call<Oauth2Token> call, Response<Oauth2Token> response) {
                if (callback != null) {
                    if (response.isSuccessful()) {
                        saveAccessToken(response.body().getAccessToken());
                        callback.onSuccess(null);
                    }
                }
            }

            @Override
            public void onFailure(Call<Oauth2Token> call, Throwable t) {
                if (callback != null) {
                    callback.onFailure(t);
                }
            }
        });
    }

    private String getAuthorizationHeader() {
        try {
            String consumerKeyAndSecret = mConsumerKey + ":" + mConsumerSecret;
            byte[] data = consumerKeyAndSecret.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.NO_WRAP);

            return "Basic " + base64;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    private void doGetTweetList(final String query, final RepositoryCallback<List<Tweet>> callback, final int retriesLeft) {
        safeStop(mStatusesCall);
        mStatusesCall = mTwitterApi.getStatuses(getAccessToken(), query);
        mStatusesCall.enqueue(new Callback<TweetStatuses>() {
            @Override
            public void onResponse(Call<TweetStatuses> call, Response<TweetStatuses> response) {
                if (callback != null) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(Tweet.buildTweets(response.body()));
                    } else {
                        if (HttpURLConnection.HTTP_UNAUTHORIZED == response.code() && retriesLeft > 0) {
                            removeAccessToken();
                            getTweetList(query, callback, retriesLeft-1);
                        } else {
                            callback.onFailure(new RuntimeException("Failed to obtain statuses"));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TweetStatuses> call, Throwable t) {
                if (callback != null) {
                    callback.onFailure(t);
                }
            }
        });
    }

    private void removeAccessToken() {
        mSharedPreferences.edit().remove(SP_KEY_ACCESS_TOKEN).apply();
    }

    private void saveAccessToken(String accessToken) {
        mSharedPreferences.edit().putString(SP_KEY_ACCESS_TOKEN, accessToken).apply();
    }

    private String getAccessToken() {
        String accessToken = mSharedPreferences.getString(SP_KEY_ACCESS_TOKEN, null);
        if (TextUtils.isEmpty(accessToken)) {
            return null;
        }
        return "Bearer " + accessToken;
    }

    public void stop() {
        safeStop(mStatusesCall);
        safeStop(mTokenCall);
    }

    private void safeStop(Call call) {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

}
