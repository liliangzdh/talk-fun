package com.talkfun.cloudlive.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.util.DimensionUtils;
import com.talkfun.cloudlive.util.DrawableUtils;
import com.talkfun.cloudlive.util.LogUtil;
import com.talkfun.cloudlive.net.NetMonitor;
import com.talkfun.cloudlive.util.TimeUtil;
import com.talkfun.sdk.offline.PlaybackDownloader;
import com.talkfun.sdk.offline.http.DownLoadManager;
import com.talkfun.sdk.offline.mode.DownloadInfoMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Wallace on 2016/7/15.
 */
public class PlayDownLoadActivity extends Activity {
    @BindView(R2.id.lv_playList)
    ListView lv_playList; //播放列表
    @BindView(R2.id.rl_editor)
    LinearLayout rl_editor; // 编辑区域
    @BindView(R2.id.tv_editor)
    TextView tv_editor;
    @BindView(R2.id.btn_all_select)
    Button btn_all_select;
    @BindView(R2.id.rl_editor_s)
    RelativeLayout rlDelete;
    @BindView(R2.id.iv_delete)
    ImageView iv_delete;
    @BindView(R2.id.pb_delete)
    ProgressBar pb_delete;
    private DownLoadListAdapter mAdapter;
    private ArrayList<String> mRemoveList;
    private boolean mIsShow = false;
    private boolean mIsSelectAll = false;  //是否全选
    private List<DownloadInfoMode> mDownloadList;

    private static final String TAG = PlayDownLoadActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
        initEvent();
    }

    private void init() {
        //获取任务列表数据
        if (!PlaybackDownloader.getInstance().isInited()) {
            PlaybackDownloader.getInstance().init(this.getApplicationContext());
        }
        mDownloadList = PlaybackDownloader.getInstance().getDownloadList();
        if (mDownloadList == null)
            mDownloadList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.playbackloadlist_layout);
        ButterKnife.bind(this);
        mAdapter = new DownLoadListAdapter();
        lv_playList.setAdapter(mAdapter);
        Drawable src = iv_delete.getDrawable();
        iv_delete.setImageDrawable(DrawableUtils.tintDrawable(src, ColorStateList.valueOf(Color.WHITE)));
        mRemoveList = new ArrayList<>();
        updateButtonsVisible();
    }

    private void updateButtonsVisible() {
        if (mDownloadList == null || mDownloadList.size() == 0) {
            rlDelete.setVisibility(View.GONE);
        } else {
            rlDelete.setVisibility(View.VISIBLE);
        }
    }

    private void initEvent() {
        btn_all_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsSelectAll = !mIsSelectAll;
                if (mIsSelectAll) {
                    mRemoveList.clear();
                    mRemoveList.addAll(PlaybackDownloader.getInstance().getPlaybackIdList());
                    btn_all_select.setText(getResources().getString(R.string.cancel_select_all));
                } else {
                    mRemoveList.clear();
                    btn_all_select.setText(getResources().getString(R.string.select_all));
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * 下载
     */
    public void startDownload(String playbackId) {
        /**判断是否联网*/
        if (!NetMonitor.isNetworkAvailable(this)) {
            Toast.makeText(PlayDownLoadActivity.this, getString(R.string.not_connect), Toast.LENGTH_SHORT).show();
            return;
        }
        PlaybackDownloader.getInstance().startDownload(playbackId);
    }

    /**
     * 播放
     */
    public void play(DownloadInfoMode infoMode) {
        Intent intent = new Intent(PlayDownLoadActivity.this, PlaybackNativeActivity.class);
        intent.putExtra("token", infoMode.token);
        intent.putExtra("id", infoMode.id);
        startActivityForResult(intent, 0);
    }

    /**
     * 暂停下载
     */
    public void pauseDownload(String playbackId) {
        PlaybackDownloader.getInstance().pauseDownload(playbackId);
    }

    public void generateStatus(int State, TextView textView, ImageView imageView, Button button, ProgressBar progressBar, TextView percentTV) {
        String StateString = "";
        int imageId = -1;
        switch (State) {
            case PlaybackDownloader.Status.STATE_DOWNLOADED:   //下载完成
                LogUtil.e("回调", "完成");
                button.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                percentTV.setVisibility(View.GONE);
                return;
            case PlaybackDownloader.Status.STATE_PAUSEDOWNLOAD:
                StateString = "继续下载";
                imageId = R.drawable.goon_selected;
                break;
            case PlaybackDownloader.Status.STATE_UNDOWNLOAD:
                StateString = "开始下载";
                imageId = R.mipmap.download_playback;
                break;
            case PlaybackDownloader.Status.STATE_DOWNLOADING:
                StateString = "暂停下载";
                imageId = R.drawable.pause_selected;
                break;
            case PlaybackDownloader.Status.STATE_WAITINGDOWNLOAD:
                StateString = "等待队列";
                imageId = R.drawable.wait_selected;
                break;
            case PlaybackDownloader.Status.STATE_DOWNLOADFAILED:
                StateString = "重新下载";
                imageId = R.drawable.reload_selected;
                break;
        }
        button.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        textView.setText(StateString);
        imageView.setImageResource(imageId);
    }

    @OnClick(R2.id.tv_editor)
    public void cancel() {
        tv_editor.setVisibility(View.GONE);
        iv_delete.setVisibility(View.VISIBLE);
        mRemoveList.clear();
        rl_editor.setVisibility(View.GONE);
        mIsShow = false;
        mIsSelectAll = false;
        btn_all_select.setText(getResources().getString(R.string.select_all));
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R2.id.iv_delete)
    public void editor() {
        iv_delete.setVisibility(View.GONE);
        tv_editor.setVisibility(View.VISIBLE);
        mIsShow = true;
        rl_editor.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R2.id.btn_delete)
    public void tv_delete() {
        if (mRemoveList != null && mRemoveList.size() == 0) {
            Toast.makeText(this, "请选中视频", Toast.LENGTH_SHORT).show();
            return;
        }
        pb_delete.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                for (int j = 0; j < mRemoveList.size(); j++) {
                    LogUtil.e("mRemoveList.get(j)", mRemoveList.get(j));
                    PlaybackDownloader.getInstance().deleteDownload(mRemoveList.get(j));
                    PlaybackDownloader.getInstance().removeObserver(mRemoveList.get(j));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_delete.setVisibility(View.VISIBLE);
                        tv_editor.setVisibility(View.GONE);
                        rl_editor.setVisibility(View.GONE);
                        pb_delete.setVisibility(View.GONE);
                        mRemoveList.clear();
                        mIsShow = false;
                        mIsSelectAll = false;
                        btn_all_select.setText(getResources().getString(R.string.select_all));
                        notifyDataSetChanged();
                        updateButtonsVisible();
                        Toast.makeText(PlayDownLoadActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    @OnClick(R2.id.iv_back)
    public void back() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlaybackDownloader.getInstance().removeAllObserver();

    }

    class DownLoadListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDownloadList.size();
        }

        @Override
        public Object getItem(int position) {

            return mDownloadList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PlayListHolder mHolder = null;

            if (convertView == null) {
                convertView = View.inflate(PlayDownLoadActivity.this, R.layout.monipreload, null);
                mHolder = new PlayListHolder();
                mHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_playback_title);
                mHolder.btn_download = (ImageView) convertView.findViewById(R.id.btn_download);
                mHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
                mHolder.cb_delete = (CheckBox) convertView.findViewById(R.id.cb_delete);
                mHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
                mHolder.tv_totalSie = (TextView) convertView.findViewById(R.id.tv_totalSie);
                mHolder.tv_download_status = (TextView) convertView.findViewById(R.id.tv_download_status);
                mHolder.btn_play = (Button) convertView.findViewById(R.id.btn_play);
                mHolder.ll_status = (LinearLayout) convertView.findViewById(R.id.ll_status);
                mHolder.iv_shrinkImage = (ImageView) convertView.findViewById(R.id.iv_shringimage);
                mHolder.tv_percent = (TextView) convertView.findViewById(R.id.tv_percent);
                convertView.setTag(mHolder);
            } else {
                mHolder = (PlayListHolder) convertView.getTag();
            }
            //在任务队列中获取任务信息
            final DownloadInfoMode generateInfo = mDownloadList.get(position);
            final String infoId = generateInfo.id;
            mHolder.setId(infoId);
            //把holder根据id添加进map里，方便移除
            //添加还未下载完成的监听
            if (generateInfo.state != PlaybackDownloader.Status.STATE_DOWNLOADED) {
                PlaybackDownloader.getInstance().addDownLoadObserver(infoId, mHolder);
            }
            //初始化进度条
            mHolder.progressBar.setMax(100);
            mHolder.progressBar.setVisibility(View.VISIBLE);
            mHolder.progressBar.setProgress((int) (generateInfo.finishSize * 1.0f / generateInfo.totalSize * 1.0f * 100));
            //拿到title
            mHolder.tv_title.setText(generateInfo.title);
            //文件总大小  完成的文件大小
            mHolder.tv_totalSie.setText(DimensionUtils.formatBytes(generateInfo.finishSize) + "/" + DimensionUtils.formatBytes(generateInfo.totalSize));
            float percent = DimensionUtils.formatPercent(generateInfo.finishSize, generateInfo.totalSize);
            mHolder.tv_percent.setText(percent + "%");
            //            //时长
            mHolder.tv_duration.setText(TimeUtil.displayHHMMSS(generateInfo.duration));
            //设置缩略图
            Bitmap shringImage = PlaybackDownloader.getInstance().getThumbnailImage(generateInfo.id, generateInfo.thumbnailImageUrl);
            if (shringImage != null) {
                mHolder.iv_shrinkImage.setImageBitmap(shringImage);
            }
            if (generateInfo.state == PlaybackDownloader.Status.STATE_DOWNLOADED) {
                Log.d(TAG, "id:" + generateInfo.id + "下载完成");
            }
            //初始化下载的状态
            generateStatus(generateInfo.state, mHolder.tv_download_status, mHolder.btn_download, mHolder.btn_play, mHolder.progressBar, mHolder.tv_percent);

            mHolder.ll_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (generateInfo.state) {
                        case PlaybackDownloader.Status.STATE_DOWNLOADED: //点击播放
                            play(generateInfo);
                            break;
                        case PlaybackDownloader.Status.STATE_WAITINGDOWNLOAD:     //暂停下载
                            pauseDownload(infoId);
                            break;
                        case PlaybackDownloader.Status.STATE_PAUSEDOWNLOAD: //点击继续下载
                        case PlaybackDownloader.Status.STATE_UNDOWNLOAD:     //开始下载
                        case PlaybackDownloader.Status.STATE_DOWNLOADFAILED:     //重新下载
                            startDownload(infoId);
                            break;
                        case PlaybackDownloader.Status.STATE_DOWNLOADING:     //暂停下载
                            pauseDownload(infoId);
                            break;
                    }
                }
            });
            mHolder.cb_delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (!mRemoveList.contains(infoId)) {
                            mRemoveList.add(infoId);
                        }
                    } else {
                        if (mRemoveList.contains(infoId)) {
                            mRemoveList.remove(infoId);
                        }
                    }
                }
            });
            //监听是否选中为删除对象
            if (mRemoveList.contains(infoId)) {
                mHolder.cb_delete.setChecked(true);
            } else {
                mHolder.cb_delete.setChecked(false);
            }

            if (mIsShow) {
                mHolder.cb_delete.setVisibility(View.VISIBLE);
            } else {
                mHolder.cb_delete.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    public void notifyDataSetChanged() {
        mDownloadList = PlaybackDownloader.getInstance().getDownloadList();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    class PlayListHolder implements DownLoadManager.DownLoadObserver {
        String id = "";

        public PlayListHolder() {

        }

        public void setId(String id) {
            this.id = id;
        }

        ImageView iv_shrinkImage;
        TextView tv_title;
        TextView tv_duration;
        TextView tv_totalSie;
        TextView tv_percent;
        TextView tv_download_status;
        ImageView btn_download;
        ProgressBar progressBar;
        CheckBox cb_delete;
        Button btn_play;
        LinearLayout ll_status;

        @Override
        public void onDownLoadInfoChange(final DownloadInfoMode info) {
            if (!info.id.equals(id)) {
                return;
            }
            Log.d(TAG, "id:" + info.id + ",totalSize:" + info.totalSize + ",finishSize:" + info.finishSize + ",state:" + info.state);
            //刷新界面
            //后台给出的大小，和实际测量的大小可能会有一些误差
            if (info.finishSize >= info.totalSize) {
                info.finishSize = info.totalSize;
            }
            //更新UI
            generateStatus(info.state, tv_download_status, btn_download, btn_play, progressBar, tv_percent);
            progressBar.setProgress((int) (info.finishSize * 1.0f / info.totalSize * 1.0f * 100));
            tv_totalSie.setText(DimensionUtils.formatBytes(info.finishSize) + "/" + DimensionUtils.formatBytes(info.totalSize));
            float percent = DimensionUtils.formatPercent(info.finishSize, info.totalSize);
            
            tv_percent.setText(percent + "%");
        }
    }
}
