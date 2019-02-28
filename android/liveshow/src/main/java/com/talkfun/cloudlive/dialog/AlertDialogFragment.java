package com.talkfun.cloudlive.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.talkfun.cloudlive.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 提示对话框
 */
public class AlertDialogFragment extends BaseDialogFragment{
    protected String message;
    protected String title;
    protected String positiveText;
    protected String negativeText;
    private AlertDialogListener mListener;
    public static final String EXTRA_DIALOG_TITLE_KEY = "extra_dialog_title";
    public static final String EXTRA_DIALOG_MESSAGE_KEY = "extra_dialog_message";
    public static final String EXTRA_DIALOG_POSITIVE_TEXT_KEY = "extra_dialog_positive_text";
    public static final String EXTRA_DIALOG_NEGATIVE_TEXT_KEY = "extra_dialog_negative_text";

    public static AlertDialogFragment newInstance(String title, String message) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        //alertDialogFragment.setStyle(STYLE_NORMAL, R.style.CustomDialog);
        Bundle bundle = new Bundle();
        putTitleParam(bundle, title);
        putMessageParam(bundle, message);
        alertDialogFragment.setArguments(bundle);
        return alertDialogFragment;
    }

    public static AlertDialogFragment newInstance(String title, String message,String positiveText,String negativeText) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        //alertDialogFragment.setStyle(STYLE_NORMAL, R.style.CustomDialog);
        Bundle bundle = new Bundle();
        putTitleParam(bundle, title);
        putMessageParam(bundle, message);
        putPositiveTextParam(bundle,positiveText);
        putNegativeTextParam(bundle,negativeText);
        alertDialogFragment.setArguments(bundle);
        return alertDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseArgs(getArguments());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(TextUtils.isEmpty(title)){
            title = "";
        }
        if(TextUtils.isEmpty(message))
            message = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        if(TextUtils.isEmpty(positiveText)){
            positiveText = "确定";
        }
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onConfirm();
                    mListener = null;
                }
                dismissAllowingStateLoss();
            }
        });

        if(!TextUtils.isEmpty(negativeText)){
            builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  /*  if (mListener != null) {
                        mListener.onConfirm();
                        mListener = null;
                    }*/
                    dismissAllowingStateLoss();
                }
            });
        }
        return builder.create();
     /*   AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.not_connect);
        builder.setTitle(R.string.tips);
        builder.setPositiveButton((R.string.goback), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton((R.string.refresh), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                HtSdk.getInstance().reload();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();*/
    }

    @NonNull
    protected static void putTitleParam(Bundle bundler, String title) {

        bundler.putString(EXTRA_DIALOG_TITLE_KEY, title);
    }

    @NonNull
    protected static void putMessageParam(Bundle bundler, String message) {

        bundler.putString(EXTRA_DIALOG_MESSAGE_KEY, message);
    }

    protected static void putPositiveTextParam(Bundle bundler, String positiveText) {

        bundler.putString(EXTRA_DIALOG_POSITIVE_TEXT_KEY, positiveText);
    }

    protected static void putNegativeTextParam(Bundle bundler, String negativeText) {

        bundler.putString(EXTRA_DIALOG_NEGATIVE_TEXT_KEY, negativeText);
    }


    protected void parseArgs(Bundle args) {
        message = args.getString(EXTRA_DIALOG_MESSAGE_KEY);
        title = args.getString(EXTRA_DIALOG_TITLE_KEY);
        positiveText = args.getString(EXTRA_DIALOG_POSITIVE_TEXT_KEY);
        negativeText = args.getString(EXTRA_DIALOG_NEGATIVE_TEXT_KEY);
    }


    public void setAlertDialogListener(AlertDialogListener listener){
        mListener = listener;
    }

    public interface AlertDialogListener {
        void onConfirm();
    }
}
