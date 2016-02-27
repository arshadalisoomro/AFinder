package me.sxwang.afinder.model;

import java.util.List;

/**
 * Created by wang on 2/27/16.
 */
public abstract class AbsFinder {

    private FileListObserver mFileListObserver;

    public abstract void updateFileList();

    protected void notifyFileListUpdated(List<FileWrapper> fileList) {
        if (mFileListObserver != null) {
            mFileListObserver.onFileListUpdated(fileList);
        }
    }

    public FileListObserver getFileListObserver() {
        return mFileListObserver;
    }

    public void setFileListObserver(FileListObserver fileListObserver) {
        mFileListObserver = fileListObserver;
    }

    public static interface FileListObserver {
        public void onFileListUpdated(List<FileWrapper> fileList);
    }
}
