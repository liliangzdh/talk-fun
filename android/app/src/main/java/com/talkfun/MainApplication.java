package com.talkfun;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.talkfun.cloudlive.LiveShowReactPackage;
import com.talkfun.cloudlive.util.ActivityStacks;
import com.talkfun.sdk.log.TalkFunLogger;
import com.talkfun.sdk.offline.PlaybackDownloader;

import java.util.Arrays;
import java.util.List;


public class MainApplication extends Application implements ReactApplication {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new LiveShowReactPackage(),
                    new DialogPackage()
            );
        }

        @Override
        protected String getJSMainModuleName() {
            return "index";
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, /* native exopackage */ false);

        //欢拓初始化。
        initPlaybackDownLoader();
    }

    public void initPlaybackDownLoader() {
        PlaybackDownloader.getInstance().init(this);
        PlaybackDownloader.getInstance().setDownLoadThreadSize(3);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();

        System.exit(0);
    }

    public static void exit() {
        /**终止应用程序对象时调用，不保证一定被调用 ,退出移除所有的下载任务*/
        ActivityStacks.getInstance().finishAllActivity();
        //释放离线下载资源
        PlaybackDownloader.getInstance().destroy();
        TalkFunLogger.release();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
