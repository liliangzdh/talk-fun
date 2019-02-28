package com.talkfun.cloudlive.dialog;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.talkfun.cloudlive.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 点播线路选择
 */
public class SwitchLineDialogFragment extends DialogFragment {
    private List<String> list;
    public static int selectItemPosition = 0;
    private GridView gv;
    private OnSelectedListener listener;
    private GridViewAdapter adapter;
    private FragmentManager fragmentManager;
    private ImageView close_iv;
    private View view;

/*    public SwitchLineDialogFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = View.inflate(getActivity(), R.layout.ht_network_choice, null);

        initView();
        initEvent();
        return view;
    }

    private void initView() {
        gv = (GridView) view.findViewById(R.id.gv_operator);
        adapter = new GridViewAdapter(getActivity(), list);
        gv.setAdapter(adapter);
        close_iv = (ImageView) view.findViewById(R.id.close_iv);
        TextView tvDialogTip = (TextView) view.findViewById(R.id.tv_operator_tip);
        TextView tvDialogTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        TextView tvSpeed = (TextView)view.findViewById(R.id.tv_dialog_speed);
        tvSpeed.setVisibility(View.GONE);
        tvDialogTitle.setText(getResources().getString(R.string.ht_switch_line));
        tvDialogTip.setText(getResources().getString(R.string.ht_switch_line_tip));
    }

    private void initEvent() {
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //view.setSelected(true);
                if (selectItemPosition != position) {
                    selectItemPosition = position;
                    listener.onSelected(list.get(position));
                    adapter.notifyDataSetChanged();
                }
                dismiss();
            }
        });
    }

    public void setData(List<String> data, OnSelectedListener listener) {
        this.listener = listener;
        if (list == null)
            list = new ArrayList<>();
        if (data == null) {
            list.clear();
            list.add("1");
            selectItemPosition = 0;  //选中位置重新为0
            notifyDataSetChanged();
        } else {
            if (!list.containsAll(data)) {   //如果线路地址内容不一样
                list.clear();
                list.addAll(data);
                selectItemPosition = 0;  //选中位置重新为0
                notifyDataSetChanged();
            }
        }
        //show(fragmentManager, "");
    }

    public void notifyDataSetChanged() {
        if (adapter == null) {
            adapter = new GridViewAdapter(getActivity(), list);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    class GridViewAdapter extends BaseAdapter {
        private List<String> list;
        private Context context;

        public GridViewAdapter(Context context, List<String> list) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.ht_network_item, null);
                viewHolder = new ViewHolder();
                viewHolder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
                viewHolder.iconIv = (ImageView) convertView.findViewById(R.id.icon_iv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.iconIv.setVisibility(View.GONE);
            viewHolder.nameTv.setText("线路" + (position + 1));

            if (position == selectItemPosition) {
                convertView.setBackgroundResource(R.drawable.ht_change_network_selected_item_bg);
            } else {
                convertView.setBackgroundResource(R.drawable.ht_change_network_unselected_item_bg);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView nameTv;
            ImageView iconIv;
        }
    }

    public interface OnSelectedListener {
        void onSelected(String url);
    }
}
