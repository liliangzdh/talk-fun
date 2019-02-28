package com.talkfun;

import android.content.Intent;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class DialogModule extends ReactContextBaseJavaModule {

    public DialogModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "DialogModule";
    }

    @ReactMethod
    public void showDialog(String message) {
        // 原生方法内容
        getCurrentActivity().startActivity(new Intent(getReactApplicationContext(),TestActivity.class));
    }
}
