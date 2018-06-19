package com.ValentineRutto.SearchTwitter.network;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateDeserializer implements JsonDeserializer<Date> {

    private static final String LOG_TAG = "DateDeserializer";

    @Override
    public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        String date = element.getAsString();

        String twitterFormat="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        formatter.setLenient(true);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Failed to parse Date due to:", e);
            return null;
        }
    }
}
