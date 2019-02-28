package com.talkfun.cloudlive.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.adapter.AlbumAdapter;
import com.talkfun.sdk.module.AlbumItemEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@li} interface
 * to handle interaction events.
 */
public class PlaybackAlbumFragment extends PlaybackBaseFragment {
    @BindView(R2.id.album_list)
    public ListView albumListView;
    private AlbumAdapter adapter;
    private List<AlbumItemEntity> albumList;
    /**当前播放视频索引*/
    private int currentIndex;
    private OnAlbumItemSelectedListener itemSelectedListener;

    public PlaybackAlbumFragment(){
        this.albumList = new ArrayList<AlbumItemEntity>();
    }

    public static PlaybackAlbumFragment create(String tag) {
        PlaybackAlbumFragment fragment = new PlaybackAlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PlaybackAlbumFragment create(String tag,ArrayList<AlbumItemEntity> list) {
        PlaybackAlbumFragment fragment = new PlaybackAlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putSerializable("list",list);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getSerializable("list") != null){
            albumList.clear();
            albumList.addAll((ArrayList<AlbumItemEntity>)getArguments().getSerializable("list"));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playback_album_fragment_layout,null);
        ButterKnife.bind(this, view);
        adapter = new AlbumAdapter(getActivity(), currentIndex);
        adapter.setItems(albumList);
        albumListView.setAdapter(adapter);
        albumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentIndex == position)
                    return;
                setCurrentIndex(position);
                if (itemSelectedListener != null) {
                    itemSelectedListener.onAlbumItemSelected(position);
                }
            }
        });
        return view;
    }


    public void setOnAlbumItemSelectedListener(OnAlbumItemSelectedListener listener ) {
        this.itemSelectedListener = listener;
    }

    /**
     * 设置专辑列表数据,如果之前有数据，先清除再添加
     * @param list
     */
    public void setAlbumList(List<AlbumItemEntity> list){
        if(albumList.size() > 0)
            albumList.clear();
        if(list != null && list.size() > 0)
            albumList.addAll(list);
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }


    /**
     * 追加专辑列表数据，不清除之前数据
     * @param list
     */
    public void appendAlbumList(List<AlbumItemEntity> list){
        if(list != null && list.size() > 0) {
            albumList.addAll(list);
            if(adapter != null)
                adapter.notifyDataSetChanged();
        }
    }

    /**
     * 追加专辑数据
     * @param item
     */
    public void appendAlbumItem(AlbumItemEntity item){
        if(item != null) {
            albumList.add(item);
            if(adapter != null)
                adapter.notifyDataSetChanged();
        }
    }

    /**
     * 清除专辑数据
     */
    public void clear(){
        if(albumList.size()>0){
            albumList.clear();
            if(adapter != null)
                adapter.notifyDataSetChanged();
        }
        currentIndex = 0;
   }


    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        if(adapter != null){
            adapter.setCurrentIndex(currentIndex);
        }
    }

    @Override
    void updateAdapter() {
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    void getMoreData() {

    }

    @Override
    void startAutoScroll() {

    }

    @Override
    void resetAdapterData() {

    }

    public interface OnAlbumItemSelectedListener {
        void onAlbumItemSelected(int position);
    }
}
