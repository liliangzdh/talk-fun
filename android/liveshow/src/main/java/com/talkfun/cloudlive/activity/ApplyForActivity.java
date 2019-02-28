package com.talkfun.cloudlive.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.consts.MainConsts;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ApplyForActivity extends BaseActivity {
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.ll_webView_rootLayout)
    LinearLayout rootLayout;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_for_layout);
        ButterKnife.bind(this);
        toolbar.setTitle(getString(R.string.goback));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.go_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //为防止内存泄漏，不要再xml中定义webview   -- 最好该Activity另启一个进程来运行
        webView = new WebView(this.getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(layoutParams);
        rootLayout.addView(webView);

        webView.loadUrl(MainConsts.APPLY_FOR_URL);
        //支持获取手势焦点，输入用户名、密码或其他
        webView.requestFocusFromTouch();
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);  //支持js
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            rootLayout.removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
