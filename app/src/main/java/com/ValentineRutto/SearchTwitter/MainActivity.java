package com.ValentineRutto.SearchTwitter;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ValentineRutto.SearchTwitter.repository.TwitterRepository;

public class MainActivity extends AppCompatActivity
        implements SearchFragment.SearchFragmentListener,
        SearchView.OnQueryTextListener {

    private static final java.lang.String QUERY_KEY = "QUERY_KEY";
    private SearchFragment mFragment;
    private MenuItem mSearchItem;
    private String mLastSubmittedQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.searchFragment);
        if (savedInstanceState != null) {
            mLastSubmittedQuery = savedInstanceState.getString(QUERY_KEY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSearchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setQuery(mLastSubmittedQuery, false);
            }
        });

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUERY_KEY, mLastSubmittedQuery);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mLastSubmittedQuery = query;
        if (mFragment.isVisible()) {
            mFragment.onQueryTextSubmit(query);
            MenuItemCompat.collapseActionView(mSearchItem);
            return true;
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    @Override
    public TwitterRepository getRepository() {
        return ((TwitterSearchApp) getApplication()).getRepository();
    }

    @Override
    public void requestSearchViewFocus() {
        MenuItemCompat.expandActionView(mSearchItem);
    }
}
