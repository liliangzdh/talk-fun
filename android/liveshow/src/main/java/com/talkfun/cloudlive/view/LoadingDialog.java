package com.talkfun.cloudlive.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.imageload.GlideImageLoader;

/**
 * Created by asus on 2016/9/2.
 */
public class LoadingDialog extends ProgressDialog {

    private ImageView loadingView;

    public LoadingDialog(Context context) {
        super(context, R.style.custom_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        initView();
    }


    private void initView() {
        setContentView(R.layout.loading_dialog);
        loadingView = (ImageView) findViewById(R.id.iv_loading);
        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.mipmap.loading_placeholder);
//        Glide.with(getContext()).asGif().load(R.mipmap.loading).apply(requestOptions).into(loadingView);
        GlideImageLoader.create(loadingView).load(R.mipmap.loading, requestOptions);
    }

    @Override
    public void onStart() {
        super.onStart();
        //animationsContainer.start();
        Window window = getWindow();
        WindowManager.LayoutParams windowParams = getWindow().getAttributes();
        windowParams.dimAmount = 0.0f;
        window.setAttributes(windowParams);
    }

}
