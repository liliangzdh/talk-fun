package com.talkfun.cloudlive.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.dialog.AlertDialogFactory;
import com.talkfun.cloudlive.dialog.AlertDialogFragment;
import com.talkfun.cloudlive.updateappmodule.IUpdateManager;
import com.talkfun.cloudlive.updateappmodule.UpdateManager;
import com.talkfun.cloudlive.updateappmodule.VersionInfo;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity implements IUpdateManager {
    private boolean isDownLoad = false;
    private boolean checkUpdateFlag = true;
    /* 下载包安装路径 */
    private static final String savePath = Environment.getExternalStorageDirectory()
            + "/talkfun/update_app/";
    private UpdateManager updateManager;

    private ProgressBar mProgress;
    private TextView tvProgress;
    private AlertDialog downloadDialog;

    private static final int PERMISSION_ALL = 1; //权限请求码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        /**6.0版本检测并申请开启摄像头、音频录制、扩展卡读写等权限*/
        final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        MPermissions.requestPermissions(this, PERMISSION_ALL, PERMISSIONS);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(PERMISSION_ALL)
    public void requestPermissionsSuccess() {
        processExtraData();
        checkUpdateVersion();
    }

    @PermissionDenied(PERMISSION_ALL)
    public void requestPermissionsFailed() {
      /*  AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getResources().getString(R.string.permission_rationale));
        alertDialogFragment.setAlertDialogListener(new AlertDialogFragment.AlertDialogListener() {
            @Override
            public void onConfirm() {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        alertDialogFragment.show(this.getSupportFragmentManager(), "alert");*/

        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), "", getResources().getString(R.string.permission_rationale), new AlertDialogFragment.AlertDialogListener() {
            @Override
            public void onConfirm() {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void checkUpdateVersion() {
        updateManager = new UpdateManager(this, this);
        if (checkUpdateFlag) {
            updateManager.setSavePath(savePath, VersionInfo.AppName);
            updateManager.checkVersion();
            checkUpdateFlag = false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateManager.destroy();
    }

    private void processExtraData() {
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        String action = intent.getAction();
        Uri uri = intent.getData();
        if (uri != null && !TextUtils.isEmpty(scheme) && Intent.ACTION_VIEW.equals(action)) {
            String uriString = uri.toString();
            String data = uriString.replace(scheme + "://", "");
            try {
                JSONObject object = new JSONObject(data);
                if (object != null) {
                    String liveid = object.optString("liveid");
                    String type = object.optString("type");
                    String accessToken = object.optString("access_token");
                    if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(accessToken)) {
                        Intent gotoIntent = new Intent();
                        if ("live".equals(type)) {
                            gotoIntent = intent.setClass(this, LiveNativeActivity.class);
                            gotoIntent.putExtra("token", accessToken);
                            gotoIntent.putExtra("id", liveid);
                            startActivity(gotoIntent);
                            checkUpdateFlag = false;
                        } else if ("vod".equals(type)) {
                            gotoIntent = intent.setClass(this, PlaybackNativeActivity.class);
                            gotoIntent.putExtra("token", accessToken);
                            gotoIntent.putExtra("id", liveid);
                            startActivity(gotoIntent);
                            checkUpdateFlag = false;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void updateEnd() {
        if (downloadDialog != null && downloadDialog.isShowing())
            downloadDialog.dismiss();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 更新下载进度弹创
     *
     * @param progress
     */
    private void updateDownloadDialog(int progress) {
        if (downloadDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.download));
            LayoutInflater inflater = LayoutInflater.from(this);
            View v = inflater.inflate(R.layout.progress, null);
            mProgress = (ProgressBar) v.findViewById(R.id.progress);
            tvProgress = (TextView) v.findViewById(R.id.tv_progress);
            builder.setView(v);
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    updateManager.stopDownload();
//                interceptFlag = true;
                }
            });
            tvProgress.setText(getString(R.string.zero_percent));
            downloadDialog = builder.create();
        } else {
            mProgress.setProgress(progress);
            tvProgress.setText(progress + "%");
        }
        if (!downloadDialog.isShowing())
            downloadDialog.show();
    }
}
