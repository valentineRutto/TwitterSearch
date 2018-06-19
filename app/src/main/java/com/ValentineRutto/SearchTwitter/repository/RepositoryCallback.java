package com.ValentineRutto.SearchTwitter.repository;

public interface RepositoryCallback<T> {
    void onSuccess(T object);
    void onFailure(Throwable error);
}
