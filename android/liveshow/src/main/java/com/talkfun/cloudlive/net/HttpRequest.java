package com.talkfun.cloudlive.net;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.talkfun.cloudlive.view.LoadingDialog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {
    public static final int SHOW_RESPONSE = 0;
    public static final int REQUEST_ERROR = 1;
    private IHttpRequestListener mRequestListener;
    private final LoadingDialog loadingDialog;
    private HttpURLConnection urlConnection;
    public HttpRequest(Context context) {
        loadingDialog = new LoadingDialog(context);
    }

    //Get方法：发送网络请求
    public void sendRequestWithGET(String url) {
        loadingDialog.show();
        final String requestUrl = url;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(requestUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setReadTimeout(5 * 1000);
                    urlConnection.setConnectTimeout(5 * 1000);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    int status = urlConnection.getResponseCode();
                    if (status == 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder sb = new StringBuilder();
                        String readLine;
                        while ((readLine = br.readLine()) != null) {
                            sb.append(readLine);
                        }
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = sb.toString();
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = REQUEST_ERROR;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Message message = new Message();
                    message.what = REQUEST_ERROR;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * post请求数据
     */
    public void sendRequestWithPost(String url, final String params, IHttpRequestListener listener) {
        loadingDialog.show();
        mRequestListener = listener;
        final String requestUrl = url;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(requestUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setReadTimeout(5 * 1000);
                    urlConnection.setConnectTimeout(5 * 1000);
                    urlConnection.setUseCaches(false);
                    urlConnection.setDoOutput(true);

                    // post请求的参数
                    // 获得一个输出流,向服务器写数据,默认情况下,系统不允许向服务器输出内容
                    OutputStream out = urlConnection.getOutputStream();// 获得一个输出流,向服务器写数据
                    out.write(params.getBytes());
                    out.flush();
                    out.close();

                    InputStream inputStream = urlConnection.getInputStream();
                    int status = urlConnection.getResponseCode();
                    if (status == 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder sb = new StringBuilder();
                        String readLine;
                        while ((readLine = br.readLine()) != null) {
                            sb.append(readLine);
                        }
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = sb.toString();
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = REQUEST_ERROR;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Message message = new Message();
                    message.what = REQUEST_ERROR;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setRequestListener(IHttpRequestListener l) {
        mRequestListener = l;
    }

    //新建Handler的对象，在这里接收Message，然后更新TextView控件的内容
    private HttpRequestHandler handler = new HttpRequestHandler();

    public interface IHttpRequestListener {
        void onRequestCompleted(String responseStr);

        void onIOError(String errorStr);
    }

    private class HttpRequestHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_RESPONSE:
                    loadingDialog.dismiss();
                    //parseJson((String) msg.obj);
                    if (mRequestListener != null) {
                        mRequestListener.onRequestCompleted((String) msg.obj);
                    }
                    break;
                case REQUEST_ERROR:
                    loadingDialog.dismiss();
                    if (mRequestListener != null) {
                        mRequestListener.onIOError("请求服务器失败");
                    }
                    break;
            }
        }
    }

    public void disConnect() {
        if (urlConnection != null) {
            urlConnection.disconnect();
        }

    }
}
