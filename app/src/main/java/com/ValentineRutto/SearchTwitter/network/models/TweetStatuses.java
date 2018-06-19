package com.ValentineRutto.SearchTwitter.network.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class TweetStatuses {
    private final List<Status> statuses;

    public TweetStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public static class Status {
        @SerializedName("created_at")
        private final Date createdAt;

        private final String text;

        private final User user;

        private final Entities entities;

        private static class User {
            private final String name;
            @SerializedName("screen_name")
            private final String screenName;

            private User(String name, String screenName) {
                this.name = name;
                this.screenName = screenName;
            }

            public String getName() {
                return name;
            }

            public String getScreenName() {
                return screenName;
            }
        }

        private static class Entities {
            private final List<Media> media;

            private static class Media {
                @SerializedName("media_url_https")
                private final String url;

                private final String type;

                public Media(String url, String type) {
                    this.url = url;
                    this.type = type;
                }

                public String getUrl() {
                    return url;
                }

                public boolean isPhoto() {
                    return "photo".equalsIgnoreCase(type) && !TextUtils.isEmpty(getUrl());
                }
            }

            public Entities(List<Media> media) {
                this.media = media;
            }
        }

        public Status(Date createdAt, String text, User user, Entities entities) {
            this.createdAt = createdAt;
            this.text = text;
            this.user = user;
            this.entities = entities;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public String getText() {
            return text;
        }

        public String getUserName() {
            return user.getName();
        }

        public String getUserScreenName() {
            return user.getScreenName();
        }

        public String getImageUrl() {
            if (entities != null && entities.media != null) {
                for (Entities.Media media : entities.media) {
                    if (media.isPhoto()) {
                        return media.getUrl();
                    }
                }
            }
            return null;
        }
    }

    public List<Status> getStatuses() {
        return statuses;
    }
}

