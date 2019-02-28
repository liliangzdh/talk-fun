package com.talkfun.cloudlive.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.consts.MainConsts;
import com.talkfun.cloudlive.imageload.GlideImageLoader;
import com.talkfun.cloudlive.util.TimeUtil;
import com.talkfun.sdk.module.AlbumItemEntity;
import com.talkfun.sdk.offline.PlaybackDownloader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by asus on 2016/5/13.
 */
public class AlbumAdapter extends BAdapter<AlbumItemEntity> {
    /**
     * 专辑数据列表
     */
//    private List<AlbumItemEntity> albumList;
//    private LayoutInflater layoutInflater;
//    private Context context;
    private int currentIndex;

    public AlbumAdapter(Context context, List<AlbumItemEntity> list) {
        this(context, 0);
    }

    public AlbumAdapter(Context context, int currentIndex) {
        super(context);
//        this.context = context;
//        this.albumList = list;
//        layoutInflater = LayoutInflater.from(context);
        this.currentIndex = currentIndex;
    }

//    @Override
//    public int getCount() {
//        return albumList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return albumList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            View view = loadView(R.layout.album_item_layout, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
            convertView = view;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AlbumItemEntity albumItem = itemList.get(position);

        holder.setTitle(albumItem.getTitle());
        holder.setLiveName(albumItem.getLiveName());
        holder.setDuration(TimeUtil.displayHHMMSS(albumItem.getDuration()));
        holder.setThumb(albumItem.getThumbSrc());
        holder.setPlayingLabelVisible(currentIndex == position);
        //convertView.setSelected(currentIndex == position);
        if (currentIndex == position) {
            convertView.setBackgroundResource(R.color.session_selected_color);
        } else {
            convertView.setBackgroundResource(R.color.transparent);
        }
        return convertView;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        notifyDataSetChanged();
    }


    class ViewHolder {
        @BindView(R2.id.thumbnail)
        ImageView thumbIv;
        /**
         * 专辑缩略图
         */
        @BindView(R2.id.tv_title)
        TextView titleTv;
        /**
         * 专辑标题view
         */
        @BindView(R2.id.live_name)
        TextView nameTv;
        /**
         * 专辑直播者view
         */
        @BindView(R2.id.duration)
        TextView durationTv;
        /***专辑时长view*/
        @BindView(R2.id.playing_label)
        TextView labelTv;
        @BindView(R2.id.item_layout)
        RelativeLayout itemLayout;

        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }

        /**
         * 设置专辑标题
         *
         * @param title
         */
        public void setTitle(String title) {
            titleTv.setText(title);
        }

        /**
         * 设置专辑直播者信息
         *
         * @param name
         */
        public void setLiveName(String name) {
            nameTv.setText(name);
        }

        /**
         * 设置专辑时长
         *
         * @param duration
         */
        public void setDuration(String duration) {

            durationTv.setText(duration);
        }

        public void setThumb(String src) {
            if (!TextUtils.isEmpty(src)) {
                String thumbnailPath = PlaybackDownloader.getInstance().getThumbnailPath(MainConsts.PlayBackID, src);
                if (!TextUtils.isEmpty(thumbnailPath)) {
//                    Glide.with(context).load("file://" + thumbnailPath).into(thumbIv);
                    GlideImageLoader.create(thumbIv).loadLocalImage(thumbnailPath, R.mipmap.portrait);
                } else {
                    GlideImageLoader.create(thumbIv).load(src);
//                    Glide.with(context).load(src).into(thumbIv);
                }
            }
        }

        public void setPlayingLabelVisible(boolean v) {
            int visibility = v ? View.VISIBLE : View.GONE;
            labelTv.setVisibility(visibility);
            // itemLayout.setSelected(v);
        }

    }
}
