package com.example.android.newsapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    private static final String LOG_TAG = NewsActivity.class.getName();

    // Guardian request example : https://content.guardianapis.com/search?format=json&q=[query_content]&api-key=5901ab5f-bf03-482e-9d43-757092b8bbb6
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?format=json";
    private static final String API_KEY = "5901ab5f-bf03-482e-9d43-757092b8bbb6";
    private static final String RECENT_NEWS_PAGE_SIZE = "20";
    private static final String QUERY_NEWS_PAGE_SIZE = "10";

    private static final int NEWS_LOADER_ID = 1;
    private static final String KEY_QUERY = "key_query";

    /**
     * Adapter for the list of news
     */
    private NewsAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        final SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(LOG_TAG, "onQueryTextSubmit() query : " + query);

                Bundle bundle = new Bundle();
                bundle.putString(KEY_QUERY, query);
                loadNewsData(bundle);

                hideSoftInput(searchView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(LOG_TAG, "onQueryTextChange() newText : " + newText);

                if (TextUtils.isEmpty(newText)) {
                    loadNewsData(new Bundle());
                }
                return true;
            }
        });
        searchView.setFocusable(false);

        ListView newsListVIew = findViewById(R.id.list);

        mEmptyTextView = findViewById(R.id.empty_view);
        newsListVIew.setEmptyView(mEmptyTextView);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListVIew.setAdapter(mAdapter);

        newsListVIew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News currentNews = mAdapter.getItem(position);
                Uri newsUri = Uri.parse(currentNews.getWebUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(websiteIntent, PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;
                Log.d(LOG_TAG, "onItemClick() isIntentSafe : " + isIntentSafe);

                if (isIntentSafe) {
                    startActivity(websiteIntent);
                }
            }
        });

        loadNewsData(null);
    }

    private void loadNewsData(Bundle bundle) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // If there is a network connection, fetch data
            if (bundle == null) {
                getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
            } else {
                getLoaderManager().restartLoader(NEWS_LOADER_ID, bundle, this);
            }
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyTextView.setText(R.string.no_internet_connection);
        }
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int i, @Nullable Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLoader()");

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();

        String query = "";
        if (bundle != null && bundle.containsKey(KEY_QUERY)) {
            query = bundle.getString(KEY_QUERY);
            Log.d(LOG_TAG, "onCreateLoader() query : " + query);
        }

        if (TextUtils.isEmpty(query)) {
            uriBuilder.appendQueryParameter("page-size", RECENT_NEWS_PAGE_SIZE);
        } else {
            uriBuilder.appendQueryParameter("q", query);
            uriBuilder.appendQueryParameter("page-size", QUERY_NEWS_PAGE_SIZE);
        }

        uriBuilder.appendQueryParameter("api-key", API_KEY);

        Log.d(LOG_TAG, "onCreateLoader() uri : " + uriBuilder.toString());
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> news) {
        Log.i(LOG_TAG, "onLoadFinished()");

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news found."
        mEmptyTextView.setText(R.string.no_news);

        // Add news data to the adapter
        mAdapter.clear();
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.i(LOG_TAG, "onLoaderReset()");
        mAdapter.clear();
    }

    private void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}