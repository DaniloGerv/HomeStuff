package com.univpm.homestuff.services;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.univpm.homestuff.R;
import com.univpm.homestuff.callbacks.ResponseCallBack;

public class AlertService {

    private static Context ct;

    public AlertService(Context ct)
    {
        this.ct=ct;
    }

    public void defaultErrorData()
    {
        new MaterialAlertDialogBuilder(ct)
                .setTitle(R.string.errore)
                .setMessage(R.string.erroreDati)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
    public void errorAlert(int title,int message)
    {
        new MaterialAlertDialogBuilder(ct)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public void questionAlert(int title, int message, final ResponseCallBack myCallBack)
    {
        new MaterialAlertDialogBuilder(ct)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myCallBack.onCallback(true);
                    }
                })
                .setNegativeButton(R.string.cancella, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myCallBack.onCallback(false);
                    }
                })
                .show();
    }

    public void successAlert(int title,int message)
    {
        new MaterialAlertDialogBuilder(ct)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}
