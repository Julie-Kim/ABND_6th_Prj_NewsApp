package com.example.android.newsapp;

public class News {

    private String mSectionName;
    private String mWebPublicationDate;
    private String mWebTitle;
    private String mWebUrl;

    public News(String sectionName, String webPublicationDate, String webTitle, String webUrl) {
        mSectionName = sectionName;
        mWebPublicationDate = webPublicationDate;
        mWebTitle = webTitle;
        mWebUrl = webUrl;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getWebPublicationDate() {
        return mWebPublicationDate;
    }

    public String getWebTitle() {
        return mWebTitle;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    @Override
    public String toString() {
        return "News{" +
                "mSectionName='" + mSectionName + '\'' +
                ", mWebPublicationDate='" + mWebPublicationDate + '\'' +
                ", mWebTitle='" + mWebTitle + '\'' +
                ", mWebUrl='" + mWebUrl + '\'' +
                '}';
    }
}