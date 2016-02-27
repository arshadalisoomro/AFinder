package me.sxwang.afinder.model;

import android.content.Context;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;

import java.util.regex.Pattern;

import me.sxwang.afinder.R;

/**
 * Created by wang on 2/23/16.
 */
public class FileType {

    public static Pattern getFileNamePattern() {
        return null;
    }

    public static boolean testFileName() {
        return false;
    }

    public DrawableTypeRequest loadIcon(Context context, FileWrapper wrapper) {
        String fileName = wrapper.getFile().getName();
        if (wrapper.getFile().isDirectory()) {
            return Glide.with(context).load(R.drawable.ic_folder);
        } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
            return Glide.with(context).load(wrapper.getFile());
        }
        return Glide.with(context).load(R.mipmap.ic_launcher);
    }

    public static FileType resolve(String name, boolean directory) {
        FileType fileType = new FileType();
        return fileType;
    }
}
