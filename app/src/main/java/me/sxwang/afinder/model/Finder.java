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
    private FileWrapper mCurrentPath;
    private List<FileWrapper> mFileList;

    public Finder() {
    }

    public FileWrapper getCurrentPathWrapper() {
        return mCurrentPath;
    }

    public String getCurrentPath() {
        return mCurrentPath.getFile().getAbsolutePath();
    }

    public boolean goUp() throws IOException {
        String parent = mCurrentPath.getFile().getParent();
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
        mCurrentPath = FileWrapper.wrap(file);
        updateFileList();
    }

    public List<FileWrapper> getFileList() {
        return mFileList;
    }

    public void updateFileList() {
        File[] files = mCurrentPath.getFile().listFiles();
        if (files == null) {
            mFileList = Collections.emptyList();
            return;
        }

        mFileList = new ArrayList<>();
        for (File file : files) {
            mFileList.add(FileWrapper.wrap(file));
        }

        sortFileList();
    }

    public void sortFileList() {
        sortFileList(new DefaultFileComparator());
    }

    public void sortFileList(Comparator<FileWrapper> comparator) {
        Collections.sort(mFileList, comparator);
        notifyFileListUpdated(mFileList);
    }

    public boolean createFile(String name, boolean isDirectory) throws IOException {
        File file = new File(mCurrentPath.getFile(), name);
        boolean success = isDirectory ? file.mkdir() : file.createNewFile();
        if (success) {
            updateFileList();
        }
        return success;
    }

    public boolean deleteFiles(List<FileWrapper> files) {
        boolean result = true;
        for (FileWrapper wrapper : files) {
            result = result && wrapper.getFile().delete();
        }
        return result;
    }

    public static class DefaultFileComparator implements Comparator<FileWrapper> {
        @Override
        public int compare(FileWrapper lhs, FileWrapper rhs) {
            int l = lhs.getFile().isDirectory() ? 0 : 1;
            int r = rhs.getFile().isDirectory() ? 0 : 1;
            if (l - r != 0) {
                return l - r;
            }
            return lhs.getFile().compareTo(rhs.getFile());
        }
    }
}
