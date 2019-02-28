package com.talkfun.cloudlive.dialog;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import java.lang.ref.WeakReference;


/**
 * Created by asus on 2018/1/15.
 */

public class AlertDialogFactory {

    private static WeakReference<DialogFragment> wrDialogFragment;

    public static void showAlertDialog(FragmentManager fragmentManager, String title, String message,final AlertDialogFragment.AlertDialogListener listener) {
        dismiss();

        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(title,message);

        alertDialogFragment.setAlertDialogListener(new AlertDialogFragment.AlertDialogListener() {
            @Override
            public void onConfirm() {
                dismiss();
                if(listener != null){
                    listener.onConfirm();
                }
            }
        });
        wrDialogFragment = new WeakReference<DialogFragment>(alertDialogFragment);
        alertDialogFragment.show(fragmentManager,"alert");
    }

    public static void showAlertDialog(FragmentManager fragmentManager, String title, String message, String positiveText,String negativeText,final AlertDialogFragment.AlertDialogListener listener) {
        dismiss();
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(title,message,positiveText,negativeText);
        alertDialogFragment.setAlertDialogListener(new AlertDialogFragment.AlertDialogListener() {
            @Override
            public void onConfirm() {
                dismiss();
                if(listener != null){
                    listener.onConfirm();
                }
            }
        });
        wrDialogFragment = new WeakReference<DialogFragment>(alertDialogFragment);
        alertDialogFragment.show(fragmentManager,"alert");
    }

    public static void dismiss(){
        if(wrDialogFragment != null ){
            DialogFragment dialogFragment = wrDialogFragment.get();
            if(dialogFragment != null)
                dialogFragment.dismissAllowingStateLoss();
            wrDialogFragment = null;
        }
    }
}
