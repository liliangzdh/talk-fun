package com.talkfun.cloudlive.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.consts.MainConsts;
import com.talkfun.cloudlive.imageload.GlideImageLoader;
import com.talkfun.cloudlive.util.TimeUtil;
import com.talkfun.sdk.module.ChapterEntity;
import com.talkfun.sdk.offline.PlaybackDownloader;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SectionAdapter extends BAdapter<ChapterEntity> {
    private int blackBoald = 1;
    private HashMap<Integer, Integer> blockboald = new HashMap<>();

    public SectionAdapter(Context context) {
        super(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = loadView(R.layout.section_item_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChapterEntity chapterEntity = itemList.get(position);
        String page = chapterEntity.getPage();
        String pageMessage = context.getResources().getString(R.string.ht_page);

        String course = chapterEntity.getCourse();

        if (TextUtils.isDigitsOnly(page) && Integer.valueOf(page) > 10000) {
            viewHolder.previewImg.setImageResource(R.mipmap.default_blackboard);
            pageMessage = "";
        } else {
            String thumbnailPath = PlaybackDownloader.getInstance().getThumbnailPath(MainConsts.PlayBackID, chapterEntity.getThumb());
            if (!TextUtils.isEmpty(thumbnailPath)) {
                GlideImageLoader.create(viewHolder.previewImg).loadLocalImage(thumbnailPath, R.mipmap.default_blackboard);
//                Glide.with(context).load("file://" + thumbnailPath).into(viewHolder.previewImg);
            } else {
                GlideImageLoader.create(viewHolder.previewImg).load(chapterEntity.getThumb());
//                Glide.with(context).load(chapterEntity.getThumb()).into(viewHolder.previewImg);
            }
        }
        viewHolder.pageTv.setText(String.format(pageMessage, page));
        viewHolder.courseTv.setText(course);
        if (selectItem == position) {
            viewHolder.sectionTime.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.current_section_bg));
            viewHolder.sectionTime.setText("播放中");
            viewHolder.sectionTime.setTextColor(Color.WHITE);
            convertView.setSelected(true);
            convertView.setBackgroundResource(R.color.session_selected_color);
        } else {
            viewHolder.sectionTime.setBackgroundDrawable(null);
            viewHolder.sectionTime.setText(TimeUtil.displayHHMMSS(chapterEntity.getTime()));
            viewHolder.sectionTime.setTextColor(context.getResources().getColor(R.color.playback_session_time));
            convertView.setSelected(false);
            convertView.setBackgroundResource(R.color.transparent);
        }

        return convertView;
    }

    class ViewHolder {
        @BindView(R2.id.preview)
        ImageView previewImg;
        @BindView(R2.id.page)
        TextView pageTv;
        @BindView(R2.id.course)
        TextView courseTv;
        @BindView(R2.id.section_time)
        TextView sectionTime;

        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
