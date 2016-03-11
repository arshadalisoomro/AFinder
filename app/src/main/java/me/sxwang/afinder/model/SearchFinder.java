package me.sxwang.afinder.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2/27/16.
 */
public class SearchFinder extends AbsFinder {

    File mCurrentPath;
    List<File> mFileList = new ArrayList<>();

    public File getCurrentPath() {
        return mCurrentPath;
    }

    public void setCurrentPath(File currentPath) {
        mCurrentPath = currentPath;
    }

    public List<File> getFileList() {
        return mFileList;
    }

    public void query(String query) {
        List<File> result = new ArrayList<>();
        search(query.toLowerCase(), mCurrentPath, result);
        mFileList = result;

        notifyFileListUpdated();
    }

    private void search(String query, File dir, List<File> result) {
        if (dir.canRead() && dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (File c : children) {
                if (c.getName().toLowerCase().contains(query)) {
                    result.add(dir);
                }
                search(query, c, result);
            }
        }
    }
}
