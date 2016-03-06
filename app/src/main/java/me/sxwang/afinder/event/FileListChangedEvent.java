package me.sxwang.afinder.event;

import java.util.List;

import me.sxwang.afinder.model.FileWrapper;

/**
 * Created by wang on 3/6/16.
 */
public class FileListChangedEvent {
    public List<FileWrapper> mFileList;
    public int mFinderId;

    public FileListChangedEvent(List<FileWrapper> fileList, int finderId) {
        mFileList = fileList;
        mFinderId = finderId;
    }
}
