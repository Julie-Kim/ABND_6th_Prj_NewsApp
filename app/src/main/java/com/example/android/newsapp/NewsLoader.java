package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

class NewsLoader extends AsyncTaskLoader<List<News>> {
    private static final String LOG_TAG = NewsLoader.class.getName();

    private String mUrl;

    NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "onStartLoading()");

        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        Log.i(LOG_TAG, "loadInBackground()");

        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of news.
        return QueryUtils.fetchNewsData(mUrl);
    }
}