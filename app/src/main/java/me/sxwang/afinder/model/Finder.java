package me.sxwang.afinder.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wang on 2/24/16.
 */
public class Finder extends AbsFinder {
    private File mCurrentPath;
    private List<File> mFileList;
    private String mCopiedPath;

    public String getCurrentPath() {
        return mCurrentPath.getAbsolutePath();
    }

    public boolean goUp() throws IOException {
        String parent = mCurrentPath.getParent();
        if (parent != null) {
            cd(parent);
            return true;
        }
        return false;
    }

    public void cd(String path) throws IOException {
        File file = new File(path);
        if (!file.canRead()) {
            throw new IOException("Permission denied");
        }
        mCurrentPath = file;
        updateFileList();
    }

    @Override
    public List<File> getFileList() {
        return mFileList;
    }

    public void updateFileList() {
        File[] files = mCurrentPath.listFiles();
        if (files == null) {
            mFileList = Collections.emptyList();
            return;
        }

        mFileList = new ArrayList<>();
        for (File file : files) {
            mFileList.add(file);
        }

        sortFileList();
    }

    public void sortFileList() {
        sortFileList(new DefaultFileComparator());
    }

    public void sortFileList(Comparator<File> comparator) {
        Collections.sort(mFileList, comparator);
        notifyFileListUpdated();
    }

    public boolean createFile(String name, boolean isDirectory) throws IOException {
        File file = new File(mCurrentPath, name);
        boolean success = isDirectory ? file.mkdir() : file.createNewFile();
        if (success) {
            updateFileList();
        }
        return success;
    }

    public boolean deleteFiles(List<File> files) {
        boolean result = true;
        for (File file : files) {
            result = result && rm(file);
        }
        updateFileList();
        return result;
    }

    public void copyPathOnly(String path) {
        mCopiedPath = path;
    }

    public void paste() {
        paste(mCurrentPath);
    }

    public void paste(File directory) {
        
    }

    private boolean rm(File file) {
        if (file.canRead() && file.isDirectory()) {
            File[] children = file.listFiles();
            for (File c : children) {
                rm(c);
            }
        }
        return file.delete();
    }

    public boolean renameFileTo(File file, String newName) {
        boolean result = file.renameTo(new File(file.getParent(), newName));
        updateFileList();
        return result;
    }

    public static class DefaultFileComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            int l = lhs.isDirectory() ? 0 : 1;
            int r = rhs.isDirectory() ? 0 : 1;
            if (l - r != 0) {
                return l - r;
            }
            return lhs.compareTo(rhs);
        }
    }
}
