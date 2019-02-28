package com.talkfun.cloudlive.updateappmodule.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.talkfun.cloudlive.updateappmodule.VersionInfo;
import com.talkfun.cloudlive.updateappmodule.model.entity.NewVersionEntity;
import com.talkfun.cloudlive.updateappmodule.util.AppInfoUtil;
import com.talkfun.cloudlive.updateappmodule.util.UpdateManagerConst;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/6/23.
 */
public class CheckVersionIpml {
    private String newVersionCode;
    private String newVersionName;
    private String newVersionUrl;
    private String newVersionContent;
    private Context mContext;

    public CheckVersionIpml(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 更新本地信息
     */
    public void setLocalCacheAppInfo() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                UpdateManagerConst.CacheAppInfoFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putInt(VersionInfo.VersionCode, Integer.valueOf(newVersionCode));
        edt.putString(VersionInfo.VersionName, newVersionName);
        edt.putString(VersionInfo.Url, newVersionUrl);
        edt.apply();
    }

    /**
     * 联网检测网络版本
     *
     * @return
     * @throws IOException
     */
    public void checkVersion(final CheckVersionListener checkVersionListener) {
        new Thread() {
            @Override
            public void run() {
                InputStream is = null;
                try {
                    URL url = new URL(UpdateManagerConst.APP_UPDATE_SERVER_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10 * 000);
                    conn.setConnectTimeout(15 * 000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    int response = conn.getResponseCode();
                    is = conn.getInputStream();
                    if (response == 200) {
                        parseJson(readIt(is), checkVersionListener);  //解析
                    } else {
                        if (checkVersionListener != null)
                            checkVersionListener.noNewVersion();
                    }
                } catch (Exception e) {
                    if (checkVersionListener != null)
                        checkVersionListener.noNewVersion();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * Convert the InputStream into a string
     * 将InputStream 转换为字符串
     *
     * @param stream
     * @return
     * @throws IOException
     */
    private String readIt(InputStream stream) throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder strBuilder = new StringBuilder();
        String line;
        while ((line = buffer.readLine()) != null) {
            strBuilder.append(line);
        }
        return strBuilder.toString();
    }


    /**
     * 解析返回的内容
     *
     * @param jsonString
     */
    private void parseJson(String jsonString, CheckVersionListener checkVersionListener) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            newVersionCode = jsonObject.optString(VersionInfo.VersionCode);
            newVersionName = jsonObject.optString(VersionInfo.VersionName);
            newVersionUrl = jsonObject.optString(VersionInfo.Url);
            newVersionContent = jsonObject.optString(VersionInfo.VersionInfo);
//            newVersionContent = "1.添加了广播机制\n2.添加了游戏规则\n3.修复已知bug";
            //判断网络版本是否存在
            if (TextUtils.isEmpty(newVersionCode)) {
                if (checkVersionListener != null)
                    checkVersionListener.noNewVersion();
                return;
            }

            int networkVersionCode = Integer.valueOf(newVersionCode);
            //判断网络版本号是不是大于当前版本号
            int currentVersionCode = AppInfoUtil.getVerCode(mContext);
            if (networkVersionCode > currentVersionCode && currentVersionCode > 0) {
                NewVersionEntity newVersionEntity = new NewVersionEntity();
                newVersionEntity.setContent(newVersionContent);
                newVersionEntity.setUrl(newVersionUrl);
                newVersionEntity.setVersionCode(networkVersionCode);
                if (checkVersionListener != null) {
                    checkVersionListener.newVersion(newVersionEntity);
                }
            } else {
                if (checkVersionListener != null)
                    checkVersionListener.noNewVersion();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (checkVersionListener != null)
                checkVersionListener.noNewVersion();
        }
    }


    /**
     * 监听检查版本回调
     */
    public interface CheckVersionListener {
        void newVersion(NewVersionEntity newVersionEntity);

        void noNewVersion();
    }
}
