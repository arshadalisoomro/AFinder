package me.sxwang.afinder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;

import java.io.File;

/**
 * Created by wang on 3/11/16.
 */
public final class Utils {
    private Utils() {
    }

    public static Intent createIntent(File file) {
        if (file.isFile()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            String name = file.getName();
            String extName = name.substring(name.lastIndexOf(".") + 1);
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extName);

            intent.setDataAndType(Uri.fromFile(file), type);
            return intent;
        }
        return null;
    }

    public static DrawableTypeRequest loadIcon(File file, Context context) {
        if (file.isDirectory()) {
            return Glide.with(context).load(R.drawable.ic_folder);
        } else {
            return Glide.with(context).load(R.mipmap.ic_launcher);
        }
    }
}
