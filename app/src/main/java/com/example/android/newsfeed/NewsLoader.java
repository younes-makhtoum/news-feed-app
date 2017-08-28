package com.example.android.newsfeed;
import android.content.Context;
import android.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by Younes on 24/06/2017.
 */

public class NewsLoader extends AsyncTaskLoader<List<Article>> {

    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Article> loadInBackground() {
        if (mUrl == null) {
        return null;
    }

    // Perform the network request, parse the response, and extract a list of articles.
    List<Article> articles = QueryUtils.fetchArticleData(mUrl);
    return articles;
    }
}