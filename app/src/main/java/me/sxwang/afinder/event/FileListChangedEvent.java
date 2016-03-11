package me.sxwang.afinder.event;

import java.io.File;
import java.util.List;

/**
 * Created by wang on 3/6/16.
 */
public class FileListChangedEvent {
    public List<File> mFileList;
    public int mFinderId;

    public FileListChangedEvent(List<File> fileList, int finderId) {
        mFileList = fileList;
        mFinderId = finderId;
    }
}
