package me.sxwang.afinder.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.io.File;

import me.sxwang.afinder.R;

/**
 * Created by wang on 2/24/16.
 */
public class SegmentedPathView extends HorizontalScrollView implements View.OnClickListener {

    protected String mPath;
    private LinearLayout mSegmentsContainer;
    private OnPathSegmentClickListener mOnPathSegmentClickListener;

    public SegmentedPathView(Context context) {
        this(context, null);
    }

    public SegmentedPathView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentedPathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = new ContextThemeWrapper(getContext(), R.style.AppTheme)
                .obtainStyledAttributes(new int[]{android.support.design.R.styleable.AppCompatTheme_listPreferredItemHeightSmall});
        int containerHeight = ta.getDimensionPixelSize(0, 48);
        ta.recycle();

        mSegmentsContainer = new LinearLayout(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSegmentsContainer.setLayoutParams(lp);
        mSegmentsContainer.setMinimumHeight(containerHeight);

        removeAllViews();
        addView(mSegmentsContainer);
    }

    public void setPath(String path) {
        mPath = path;
        refresh();
    }

    public String getPath() {
        return mPath;
    }

    private void refresh() {
        mSegmentsContainer.removeAllViews();
        if (mPath == null) {
            return;
        }

        String[] segments = mPath.split(File.separator);
        if (segments.length == 0) {
            segments = new String[]{""};
        }
        StringBuilder pathBuilder = new StringBuilder();
        for (String s : segments) {
            Button button = new AppCompatButton(new ContextThemeWrapper(getContext(),
                    R.style.AppTheme_Widget_Button_Borderless_Colored_Small), null, 0);
            button.setAllCaps(false);
            button.setText(s + File.separator);

            pathBuilder.append(s + File.separator);
            button.setTag(pathBuilder.toString());
            button.setOnClickListener(this);

            mSegmentsContainer.addView(button);
        }
    }

    public void setOnPathSegmentClickListener(OnPathSegmentClickListener onPathSegmentClickListener) {
        mOnPathSegmentClickListener = onPathSegmentClickListener;
    }

    @Override
    public void onClick(View v) {
        if (mOnPathSegmentClickListener != null) {
            String path = (String) v.getTag();
            mOnPathSegmentClickListener.onPathSegmentClick(path);
        }
    }

    public interface OnPathSegmentClickListener {
        /**
         * Called to notify there is one path segment button was clicked.
         *
         * @param path the absolute path indicated by the path segment
         */
        void onPathSegmentClick(String path);
    }
}
