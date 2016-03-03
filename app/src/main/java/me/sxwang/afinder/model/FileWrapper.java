package me.sxwang.afinder.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;

import java.io.File;

import me.sxwang.afinder.R;

/**
 * Created by wang on 2/22/16.
 */
public class FileWrapper {
    private File mFile;

    public File getFile() {
        return mFile;
    }

    public static FileWrapper wrap(java.io.File file) {
        FileWrapper wrapper = new FileWrapper();
        wrapper.mFile = file;
        return wrapper;
    }

    public DrawableTypeRequest loadIcon(Context context) {
        if (mFile.isDirectory()) {
            return Glide.with(context).load(R.drawable.ic_folder);
        } else {
            return Glide.with(context).load(R.mipmap.ic_launcher);
        }
    }

    public Intent getIntent() {
        if (mFile.isFile()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(mFile), "");
        }
        return null;
    }
}
