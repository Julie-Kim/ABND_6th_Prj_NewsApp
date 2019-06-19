package com.example.android.newsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

class NewsAdapter extends ArrayAdapter<News> {
    private static final String LOG_TAG = NewsAdapter.class.getName();

    NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        News newsItem = getItem(position);
        if (newsItem != null) {
            ((TextView) listItemView.findViewById(R.id.section_name)).setText(newsItem.getSectionName());
            ((TextView) listItemView.findViewById(R.id.publication_date)).setText(formatDate(newsItem.getWebPublicationDate()));
            ((TextView) listItemView.findViewById(R.id.title)).setText(newsItem.getWebTitle());
        }

        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Jun 18, 2019") from a date string (i.e. "2019-06-18T00:54:25Z").
     */
    private String formatDate(String dateString) {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "formatDate() date format parsing error, e=" + e);
        }

        DateFormat formatter = new SimpleDateFormat("d MMM yyyy  hh:mm aaa", Locale.getDefault());
        return formatter.format(date);
    }
}