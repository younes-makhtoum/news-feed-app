package com.example.android.newsfeed;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>>, ItemClickListener {

    public static final String LOG_TAG = NewsActivity.class.getName();
    private static final int BOOK_LOADER_ID = 1;
    private List<Article> articlesList = new ArrayList<>();
    private NewsAdapter mAdapter;
    private String requestURL = "";
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());
        RecyclerView articleListView = (RecyclerView) findViewById(R.id.recycler_view);
        articleListView.setLayoutManager(new LinearLayoutManager(this));
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        // Create a new adapter that takes an empty list of articles as input
        mAdapter = new NewsAdapter(articlesList, R.layout.article_list_item, this);
        mAdapter.setClickListener(this);
        articleListView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        articleListView.setAdapter(mAdapter);
    }

    // The onClick implementation of the RecyclerView item click
    // The purpose is to send an intent to a web browser
    // to open a website with more information about the selected article.
    @Override
    public void onClick(View view, int position) {
        // Find the current article that was clicked on
        final Article currentArticle = articlesList.get(position);
        // Convert the String URL into a URI object (to pass into the Intent constructor)
        Uri articleUri = Uri.parse(currentArticle.getLink());
        // Create a new intent to view the article URI
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);
        // Send the intent to launch a new activity
        startActivity(websiteIntent);
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    // This method :
    // --> handles the intent sent by the search interface when the user hits the search icon after entering a query in the search bar
    // --> builds the base uri for the network request
    // --> call the doSearch() method to launch the network connection to get the article's data from the Guardian's API
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String inputQuery = intent.getStringExtra(SearchManager.QUERY).replace(' ','+');
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("https")
                    .authority("content.guardianapis.com")
                    .appendPath("search")
                    .appendQueryParameter("q", inputQuery)
                    .appendQueryParameter("show-fields", "thumbnail")
                    .appendQueryParameter("show-tags", "contributor")
                    .appendQueryParameter("api-key", "test");

            requestURL = uriBuilder.toString();
            doSearch();
        }
    }

    // This method is used to launch the network connection to get the data from the Guardian's API
    private void doSearch() {
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        View loadingIndicator = findViewById(R.id.loading_spinner);
        ImageView welcomeImage = (ImageView) findViewById(R.id.welcome_image);
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
            welcomeImage.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.VISIBLE);
        } else {
            // Otherwise, display a network issue message
            // First, hide welcome image and loading indicator so error message will be visible
            welcomeImage.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String maxArticles = sharedPrefs.getString(
                getString(R.string.settings_max_articles_key),
                getString(R.string.settings_max_articles_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        Uri baseUri = Uri.parse(requestURL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("page-size", maxArticles)
                .appendQueryParameter("order-by", orderBy);
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);
        //Clear the adapter of previous article data
        mAdapter.setArticleInfoList(null);
        // If there is a valid list of {@link Article}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.setArticleInfoList(articles);
            mAdapter.notifyDataSetChanged();
            articlesList = new ArrayList<>(articles);
        }
        else {
            // Set empty state text to display "No articles found."
            mEmptyStateTextView.setText(R.string.no_articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.setArticleInfoList(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_settings, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
