package me.sxwang.afinder.model;

import java.io.File;

/**
 * Created by wang on 2/22/16.
 */
public class FileWrapper {
    private File mFile;
    private FileType type;

    public File getFile() {
        return mFile;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public static FileWrapper wrap(java.io.File file) {
        FileWrapper wrapper = new FileWrapper();
        wrapper.mFile = file;
        wrapper.type = FileType.resolve(file.getName(), file.isDirectory());
        return wrapper;
    }
}
