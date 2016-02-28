package me.sxwang.afinder.model;

import java.util.List;

/**
 * Created by wang on 2/27/16.
 */
public class SearchFinder extends AbsFinder {

    FileWrapper mCurrentPath;
    List<FileWrapper> mFileList;

    public void query(String s) {

    }

    @Override
    public void updateFileList() {
        notifyFileListUpdated(null);
    }
}
