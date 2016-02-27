package me.sxwang.afinder.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.sxwang.afinder.R;
import me.sxwang.afinder.model.AbsFinder;
import me.sxwang.afinder.model.FileWrapper;
import me.sxwang.afinder.model.Finder;
import me.sxwang.afinder.ui.adapter.FilesAdapter;
import me.sxwang.afinder.ui.widget.SegmentedPathView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.filesList)
    ListView mListView;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.fabPopupContainer)
    ViewGroup mFabPopupContainer;

    SegmentedPathView mPathView;

    Finder mFinder;
    ListAdapter mFilesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableTransition();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();
        initView();
        initFinder();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void enableTransition() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow().setExitTransition(new Explode());
    }

    private void initFinder() {
        mFinder = new Finder();
        mFinder.setFileListObserver(new AbsFinder.FileListObserver() {
            @Override
            public void onFileListUpdated(List<FileWrapper> fileList) {
                refreshView();
            }
        });
        cd(null);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd(null);
            }
        });
    }

    private void initView() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFabPopupVisible = mFabPopupContainer.getVisibility() == View.VISIBLE;
            }
        });

        mPathView = new SegmentedPathView(this);
        mPathView.setOnPathSegmentClickListener(new SegmentedPathView.OnPathSegmentClickListener() {
            @Override
            public void onPathSegmentClick(String path) {
                cd(path);
            }
        });
        mPathView.setBackgroundColor(getResources().getColor(R.color.grey50, getTheme()));
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        mPathView.setLayoutParams(lp);
        mListView.addHeaderView(mPathView);

        mFilesAdapter = new FilesAdapter(this);
        mListView.setAdapter(mFilesAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(this);
    }

    private void cd(String path) {
        if (path == null) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        try {
            mFinder.cd(path);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshView() {
        mPathView.setPath(mFinder.getCurrentPath());
        mFilesAdapter = new FilesAdapter(this, mFinder.getFileList());
        mListView.setAdapter(mFilesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                SearchActivity.start(this, mFinder.getCurrentPath());
                return true;
            case R.id.action_paste:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            if (mFinder.goUp()) {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int realPosition = position - ((ListView) parent).getHeaderViewsCount();
        FileWrapper wrapper = (FileWrapper) mFilesAdapter.getItem(realPosition);
        if (wrapper.getFile().isDirectory()) {
            cd(wrapper.getFile().getAbsolutePath());
        }
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        int count = mListView.getCheckedItemCount();

        mode.setTitle(String.valueOf(count));
        mode.invalidate();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        int count = mListView.getCheckedItemCount();

        switch (count) {
            case 0:
                menu.findItem(R.id.action_copy).setVisible(false);
                menu.findItem(R.id.action_rename).setVisible(false);
                menu.findItem(R.id.action_delete).setVisible(false);
                break;
            case 1:
                menu.findItem(R.id.action_copy).setVisible(true);
                menu.findItem(R.id.action_rename).setVisible(true);
                menu.findItem(R.id.action_delete).setVisible(false);
                break;
            default:
                menu.findItem(R.id.action_copy).setVisible(false);
                menu.findItem(R.id.action_rename).setVisible(false);
                menu.findItem(R.id.action_delete).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_copy:
                Log.d("copy", "position: " + mListView.getCheckedItemPositions().keyAt(0));
                return true;
            case R.id.action_rename:
                Log.d("rename", "position: " + mListView.getCheckedItemPositions().keyAt(0));
                return true;
            case R.id.action_delete:
                Log.d("delete", "position: " + mListView.getCheckedItemPositions());
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }
}
