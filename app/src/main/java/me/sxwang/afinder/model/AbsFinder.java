package me.sxwang.afinder.model;

import java.util.List;

import me.sxwang.afinder.BusProvider;
import me.sxwang.afinder.event.FileListChangedEvent;

/**
 * Created by wang on 2/27/16.
 */
public abstract class AbsFinder {

    public abstract void updateFileList();

    protected void notifyFileListUpdated(List<FileWrapper> fileList) {
        BusProvider.getUIBus().post(new FileListChangedEvent(fileList, this.hashCode()));
    }
}
