package me.sxwang.afinder.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.sxwang.afinder.BusProvider;
import me.sxwang.afinder.R;
import me.sxwang.afinder.event.FileListChangedEvent;
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
    @Bind(R.id.fileFab)
    FloatingActionButton mFileFab;
    @Bind(R.id.folderFab)
    FloatingActionButton mFolderFab;

    SegmentedPathView mPathView;

    Finder mFinder;

    FilesAdapter mAdapter;

    private int typeToCreate = CREATE_FILE;
    public static final int CREATE_FILE = 0;
    public static final int CREATE_FOLDER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableTransition();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();
        initView();

        BusProvider.getUIBus().register(this);
        initFinder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getUIBus().unregister(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void enableTransition() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow().setExitTransition(new Explode());
    }

    private void initFinder() {
        mFinder = new Finder();
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
        mPathView = new SegmentedPathView(this);
        mPathView.setOnPathSegmentClickListener(new SegmentedPathView.OnPathSegmentClickListener() {
            @Override
            public void onPathSegmentClick(String path) {
                cd(path);
            }
        });
        mPathView.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.grey50, getTheme()));
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        mPathView.setLayoutParams(lp);
        mListView.addHeaderView(mPathView);

        mAdapter = new FilesAdapter(this);
        mListView.setAdapter(mAdapter);
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

    @Subscribe
    public void onFileListChanged(FileListChangedEvent fileListChangedEvent) {
        if (mFinder != null && mFinder.hashCode() == fileListChangedEvent.mFinderId) {
            refreshView();
        }
    }

    private void refreshView() {
        mPathView.setPath(mFinder.getCurrentPath());
        mAdapter.clear();
        mAdapter.addAll(mFinder.getFileList());
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        toggleSubFab();
    }

    private void toggleSubFab() {
        if (mFileFab.getVisibility() != View.VISIBLE) {
            mFileFab.show();
            mFolderFab.show();
            mFab.animate()
                    .rotation(45)
                    .setDuration(200)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
        } else {
            mFolderFab.hide();
            mFileFab.hide();
            mFab.animate()
                    .rotation(0)
                    .setDuration(200)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
        }
    }

    @OnClick({R.id.fileFab, R.id.folderFab})
    public void onSubFabClick(View view) {
        toggleSubFab();
        CharSequence title = "Create";
        switch (view.getId()) {
            case R.id.fileFab:
                title = "Create a file";
                typeToCreate = CREATE_FILE;
                break;
            case R.id.folderFab:
                title = "Create a folder";
                typeToCreate = CREATE_FOLDER;
        }
        EditTextDialogFragment.newInstance(title, "Name", new EditTextDialogFragment.OnFinishListener() {
            @Override
            public void onFinish(CharSequence text) {
                try {
                    String info;
                    if (mFinder.createFile(text.toString(), typeToCreate == CREATE_FOLDER)) {
                        info = "Success";
                    } else {
                        info = "Failed";
                    }
                    Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).show(getSupportFragmentManager(), null);
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
        FileWrapper wrapper = (FileWrapper) mListView.getAdapter().getItem(position);
        if (wrapper.getFile().isDirectory()) {
            cd(wrapper.getFile().getAbsolutePath());
        } else {
            startActivity(wrapper.getIntent());
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
                menu.findItem(R.id.action_delete).setVisible(true);
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
        SparseBooleanArray positions = mListView.getCheckedItemPositions();

        List<FileWrapper> files = new ArrayList<>();
        for (int i = 0, size = positions.size(); i < size; i++) {
            if (positions.valueAt(i)) {
                int p = positions.keyAt(i);
                FileWrapper wrapper = (FileWrapper) mListView.getAdapter().getItem(p);
                files.add(wrapper);
            }
        }

        switch (id) {
            case R.id.action_copy:
                Log.d("copy", "position: " + positions.keyAt(0));
                mode.finish();
                return true;
            case R.id.action_rename:
                Log.d("rename", "position: " + positions.keyAt(0));
                final FileWrapper wrapper = files.get(0);
                EditTextDialogFragment.newInstance("Rename", "New name", new EditTextDialogFragment.OnFinishListener() {
                    @Override
                    public void onFinish(CharSequence text) {
                        mFinder.renameFileTo(wrapper, text.toString());
                    }
                }).show(getSupportFragmentManager(), null);
                mode.finish();
                return true;
            case R.id.action_delete:
                Log.d("delete", "positions: " + positions);
                mFinder.deleteFiles(files);
                mode.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

}
