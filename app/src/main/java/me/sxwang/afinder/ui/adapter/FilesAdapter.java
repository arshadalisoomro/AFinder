package me.sxwang.afinder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import me.sxwang.afinder.R;
import me.sxwang.afinder.Utils;

/**
 * Created by wang on 2/24/16.
 */
public class FilesAdapter extends ArrayAdapter<File> {

    private static final String TAG = FilesAdapter.class.getSimpleName();

    public FilesAdapter(Context context) {
        super(context, 0);
    }

    public FilesAdapter(Context context, File[] objects) {
        super(context, 0, objects);
    }

    public FilesAdapter(Context context, List<File> objects) {
        super(context, 0, objects);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(LayoutInflater.from(getContext()), position, convertView, parent);
    }

    private View createView(LayoutInflater inflater, int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.file_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        bindView(holder, position);
        return view;
    }

    private void bindView(ViewHolder holder, int position) {
        File file = getItem(position);

        holder.mFileName.setText(file.getName());
        Utils.loadIcon(file, getContext()).fitCenter().crossFade().into(holder.mFileIcon);
    }

    static class ViewHolder {
        TextView mFileName;
        ImageView mFileIcon;

        public ViewHolder(View view) {
            mFileName = (TextView) view.findViewById(R.id.fileName);
            mFileIcon = (ImageView) view.findViewById(R.id.fileIcon);
        }
    }
}
