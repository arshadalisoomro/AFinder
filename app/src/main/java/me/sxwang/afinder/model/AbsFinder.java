package me.sxwang.afinder.model;

import java.io.File;
import java.util.List;

import me.sxwang.afinder.BusProvider;
import me.sxwang.afinder.event.FileListChangedEvent;

/**
 * Created by wang on 2/27/16.
 */
public abstract class AbsFinder {

    public abstract List<File> getFileList();

    protected void notifyFileListUpdated() {
        BusProvider.getUIBus().post(new FileListChangedEvent(getFileList(), this.hashCode()));
    }
}
