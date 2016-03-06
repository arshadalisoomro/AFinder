package me.sxwang.afinder.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

/**
 * Created by wang on 2/28/16.
 */
public class EditTextDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private CharSequence mTitle;
    private CharSequence mHint;
    private EditText mEditText;
    private OnFinishListener mOnFinishListener;

    public static EditTextDialogFragment newInstance(CharSequence title, CharSequence hint, OnFinishListener mOnFinishListener) {
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        fragment.setOnFinishListener(mOnFinishListener);

        Bundle bundle = new Bundle();
        bundle.putCharSequence("title", title);
        bundle.putCharSequence("hint", hint);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle = getArguments().getCharSequence("title");
        mHint = getArguments().getCharSequence("hint");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mEditText = new EditText(getActivity());
        mEditText.setHint(mHint);
        float density = getResources().getDisplayMetrics().density;
        int space = (int) (density * 24);
        int topSpace = (int) (density * 20);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle)
                .setView(mEditText, space, topSpace, space, space)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, this);

        return builder.create();
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        mOnFinishListener = onFinishListener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE && mOnFinishListener != null) {
            mOnFinishListener.onFinish(mEditText.getText());
        }
    }

    public interface OnFinishListener {
        void onFinish(CharSequence text);
    }
}
