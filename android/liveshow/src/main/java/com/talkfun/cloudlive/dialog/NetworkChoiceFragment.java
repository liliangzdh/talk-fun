package com.talkfun.cloudlive.dialog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.adapter.CDNLineAdapter;
import com.talkfun.sdk.module.CDNItem;
import com.talkfun.sdk.module.NetItem;
import com.talkfun.sdk.module.NetWorkEntity;
import com.talkfun.utils.checkNetUtil.CheckNetSpeed;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 网络选择
 */
public class NetworkChoiceFragment extends BaseDialogFragment implements View.OnClickListener, CheckNetSpeed.OnNetSpeedChangeListener {
    private List<NetItem> operatorList;
    private List<CDNItem> cdnList;
    private GridView gvOperator;
    private OnSelectedListener listener;
    private OperatorGridViewAdapter operatorAdapter;
    private TextView tvNetInfoTip;
    private TextView tvOperatorTip;
    private NetWorkEntity netWorkEntity;
    private View rootView;
    private ImageView ivClose;
    private int currentCDNIndex = 0;
    private int[] selectPosition = new int[]{-1, -1};
    private TextView tvDialogSpeed;
    private SegmentTabLayout gvCDNLine;
    private CDNLineAdapter cdnAdapter;
    private int numColumn = 3;
    public static final String BUNDLE_DATA_KEY = "data";

    public NetworkChoiceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater.inflate(R.layout.ht_network_choice, container);
        initView();
        initEvent();
        initData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gvCDNLine != null) {
            if (selectPosition[1] == -1) {
                gvCDNLine.setCurrentTab(currentCDNIndex);
            } else {
                gvCDNLine.setCurrentTab(selectPosition[0]);
            }

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    private void initView() {
        gvOperator = (GridView) rootView.findViewById(R.id.gv_operator);
        gvOperator.setVisibility(View.GONE);
        tvNetInfoTip = (TextView) rootView.findViewById(R.id.tv_netInfo_tip);
        tvNetInfoTip.setVisibility(View.GONE);
        tvOperatorTip = (TextView) rootView.findViewById(R.id.tv_operator_tip);
        tvOperatorTip.setVisibility(View.GONE);
        gvCDNLine = (SegmentTabLayout) rootView.findViewById(R.id.tb_cdn_line);

        ivClose = (ImageView) rootView.findViewById(R.id.close_iv);
        tvDialogSpeed = (TextView) rootView.findViewById(R.id.tv_dialog_speed);
    }

    private void initData() {
        Bundle bundle = getArguments();
        netWorkEntity = (NetWorkEntity) bundle.getSerializable(BUNDLE_DATA_KEY);
        setData(netWorkEntity);

    }

    private void initEvent() {

        ivClose.setOnClickListener(this);
        gvOperator.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition[0] = currentCDNIndex;
                selectPosition[1] = position;
                if (listener != null) {
                    listener.onSelected(currentCDNIndex, operatorList.get(position));
                }
                dismiss();
            }
        });

        gvCDNLine.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                setCurrentCDNIndex(position);
                //switchLine(position);
                if ((operatorList == null || operatorList.size() == 0) && listener != null) {
                    listener.onSelected(currentCDNIndex, null);
                    dismiss();
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        CheckNetSpeed.getInstance().startCheckNetSpeed(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R2.id.close_iv){
            dismiss();
        }
    }

    /**
     * 设置数据
     *
     * @param netWorkEntity
     */
    public void setData(NetWorkEntity netWorkEntity) {
        this.netWorkEntity = netWorkEntity;
        if (netWorkEntity == null || netWorkEntity.getCdnItems() == null || netWorkEntity.getCdnItems().size() == 0)
            return;

        if (cdnList == null)
            cdnList = new ArrayList<>();
        else
            cdnList.clear();


        if (netWorkEntity.getNetwork() != null) {
            tvNetInfoTip.setText(Html.fromHtml("你的网络运营商:" + "<font color='#eb6877'>" + netWorkEntity.getNetwork().getIsp() + "&nbsp&nbsp</font>" +
                    "IP:" + "<font color='#eb6877'>" + netWorkEntity.getNetwork().getIp() + "</font>"));
            tvNetInfoTip.setVisibility(View.VISIBLE);
        } else {
            tvNetInfoTip.setText("");
            tvNetInfoTip.setVisibility(View.GONE);
        }

//        cdnList.addAll(netWorkEntity.getCdnItems());

        if (cdnAdapter == null) {
            cdnAdapter = new CDNLineAdapter(getContext());
        }
        gvCDNLine.setVisibility(netWorkEntity.getCdnItems().size() > 1 ? View.VISIBLE : View.GONE);
        if (netWorkEntity != null && netWorkEntity.getCdnItems().size() > 0) {
            String[] tabName = new String[netWorkEntity.getCdnItems().size()];
            for (int i = 0; i < netWorkEntity.getCdnItems().size(); i++) {
                int num = i + 1;
                tabName[i] = "线路" + num;
            }
            gvCDNLine.setTabData(tabName);
        }


        ArrayList<CDNItem> cdnList = netWorkEntity.getCdnItems();
        CDNItem cdnItem = null;
        for (int i = 0, len = cdnList.size(); i < len; i++) {
            cdnItem = cdnList.get(i);
            if (cdnItem.isSelected()) {

                setCurrentCDNIndex(i);
                break;
            }
        }
    }

    public void setCurrentCDNIndex(int index) {
        if (netWorkEntity == null || netWorkEntity.getCdnItems() == null || netWorkEntity.getCdnItems().size() == 0)
            return;
        currentCDNIndex = index;
        ArrayList<NetItem> cdnOperators = netWorkEntity.getCdnItems().get(currentCDNIndex).getOperators();

        if (operatorList == null)
            operatorList = new ArrayList<>();
        else
            operatorList.clear();

//        cdnAdapter.setSelectItem(index);

        if (cdnOperators != null && cdnOperators.size() > 0) {
            operatorList.addAll(cdnOperators);
            tvOperatorTip.setVisibility(View.VISIBLE);
            gvOperator.setVisibility(View.VISIBLE);
        } else {
            tvOperatorTip.setVisibility(View.GONE);
            gvOperator.setVisibility(View.GONE);
        }

        if (operatorAdapter == null) {
            operatorAdapter = new OperatorGridViewAdapter(operatorList);
        }
        if (gvOperator.getAdapter() != operatorAdapter) {
            gvOperator.setAdapter(operatorAdapter);
        }
        operatorAdapter.notifyDataSetChanged();
    }


    //选中位置重置
    public void setOnSelectedListener(OnSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void getNetSpeedAndState(int speed, String NetState) {
        if (tvDialogSpeed != null) {
            tvDialogSpeed.setText("当前网速：" + speed + "KB/s");
        }
    }

    //**
    private class OperatorGridViewAdapter extends BaseAdapter {
        private List<NetItem> list;

        public OperatorGridViewAdapter(List<NetItem> list) {
            this.list = list;
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.ht_network_item, null);
                viewHolder = new ViewHolder();
                viewHolder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
                viewHolder.iconIv = (ImageView) convertView.findViewById(R.id.icon_iv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            NetItem item = (NetItem) getItem(position);
            if (item != null) {
                viewHolder.nameTv.setText(item.getName());
                int iconName = getResourceByReflect(item.getKey());
                if (iconName != 0) {
                    viewHolder.iconIv.setImageResource(iconName);
                    viewHolder.iconIv.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.iconIv.setVisibility(View.GONE);
                }
            }

            if (selectPosition[0] == currentCDNIndex && selectPosition[1] == position) {
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
        void onSelected(int position, NetItem item);
    }

    public void resetSelectPosition() {
        if (selectPosition != null) {
            selectPosition = new int[]{-1, -1};
        }
    }


    /**
     * 获取图片名称获取图片的资源id的方法
     *
     * @param imageName
     * @return
     */
    public int getResourceByReflect(String imageName) {
        Class drawable = R.mipmap.class;
        Field field = null;
        int r_id = 0;
        try {
            field = drawable.getField(imageName);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id = 0;
            Log.e("ERROR", "PICTURE NOT　FOUND！");
        }
        return r_id;
    }

}
