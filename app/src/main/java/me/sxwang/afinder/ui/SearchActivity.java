package me.sxwang.afinder.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import java.io.File;

import me.sxwang.afinder.BusProvider;
import me.sxwang.afinder.R;
import me.sxwang.afinder.event.FileListChangedEvent;
import me.sxwang.afinder.model.SearchFinder;
import me.sxwang.afinder.ui.adapter.FilesAdapter;

public class SearchActivity extends AppCompatActivity {

    public static final String EXTRA_PATH = SearchActivity.class.getName() + ".EXTRA_PATH";

    public static void start(Activity activity, String path) {
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putExtra(EXTRA_PATH, path);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    private SearchFinder mFinder;

    ListView mListView;
    SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableTransition();
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String path = getIntent().getStringExtra(EXTRA_PATH);
        mFinder = new SearchFinder();
        mFinder.setCurrentPath(new File(path));

        mListView = (ListView) findViewById(R.id.list);

        BusProvider.getUIBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getUIBus().unregister(this);
    }

    @Subscribe
    public void onFileListChanged(FileListChangedEvent fileListChangedEvent) {
        if (mFinder != null && mFinder.hashCode() == fileListChangedEvent.mFinderId) {
            mListView.setAdapter(new FilesAdapter(this, fileListChangedEvent.mFileList));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void enableTransition() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    mFinder.query(query);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        searchMenuItem.expandActionView();
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                supportFinishAfterTransition();
                return false;
            }
        });
        return true;
    }


}
