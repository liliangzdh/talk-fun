package com.talkfun.cloudlive.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.interfaces.IDispatchNotice;
import com.talkfun.sdk.module.NoticeEntity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 公告Fragment
 */
public class NoticeFragment extends Fragment implements IDispatchNotice {
    @BindView(R2.id.notice_tv)
    TextView noticeTv;
    @BindView(R2.id.date)
    TextView timeTv;
    //    private String tag;
    private String content;
    private String time;

    public NoticeFragment() {

    }

//    public static NoticeFragment getInstance(String tag) {
//        NoticeFragment nf = new NoticeFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("notice", tag);
//        nf.setArguments(bundle);
//        return nf;
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments().getString("notice") != null) {
//            tag = getArguments().getString("notice");
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.notice_fragment_layout, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (content != null && noticeTv != null)
            noticeTv.setText("公告 ：" + content);
        if (time != null && timeTv != null)
            timeTv.setText(time);
    }

    private final String prefix = "<p>";
    private final String suffix = "</p>";

    @Override
    public void getNotice(NoticeEntity noticeEntity) {
        if (noticeEntity != null) {
            content = noticeEntity.getContent();
            if (TextUtils.isEmpty(content))
                return;
            time = noticeEntity.getTime();
            if (content.startsWith(prefix) && content.endsWith(suffix))
                content = content.substring(content.indexOf(prefix) + prefix.length(), content.lastIndexOf(suffix));
            try {
                content = URLDecoder.decode(content, "utf-8");
                if (noticeTv != null)
                    noticeTv.setText(Html.fromHtml("<font color='#f08336'>公告 ：</font><font color='#333'>"+ content + "</font>") );
                if (timeTv != null)
                    timeTv.setText(time);
            } catch (UnsupportedEncodingException e) {
            }
        }
    }
}
