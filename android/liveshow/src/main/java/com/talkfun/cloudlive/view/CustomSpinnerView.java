package com.talkfun.cloudlive.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.cloudlive.util.DimensionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/21 0021.
 */
public class CustomSpinnerView extends PopupWindow {
    @BindView(R2.id.lv_spinner_list)
    ListView lvSpinnerList;
    private Context mContext;
    private String[] items;
    private int selectedPosition = 0;

    public CustomSpinnerView(Context context) {
        this(context, null);
    }

    public CustomSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
        initView();
        initEvent();
    }

    private void init() {
        items = mContext.getResources().getStringArray(R.array.mode);
    }

    private void initView() {
        View view = View.inflate(mContext, R.layout.custom_spinner_layout, null);
        ButterKnife.bind(this, view);
        setContentView(view);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setWidth(DimensionUtils.dip2px(mContext, 120));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        lvSpinnerList.setAdapter(new SpinnerAdapter());
    }

    private void initEvent() {
        lvSpinnerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPosition = i;
                if (spinnerListener != null)
                    spinnerListener.onItemClick(selectedPosition);
                dismiss();
            }
        });

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (spinnerListener != null)
                    spinnerListener.onDismiss();
            }
        });
    }

    public void setSelectedPosition(int position){
        selectedPosition = position;
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }


    class SpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = new TextView(mContext);
            }
            TextView textView = (TextView) convertView;
            textView.setPadding(DimensionUtils.dip2px(mContext, 5), DimensionUtils.dip2px(mContext, 5), DimensionUtils.dip2px(mContext, 5), DimensionUtils.dip2px(mContext, 5));
            textView.setText(items[i]);
            textView.setBackgroundColor(Color.WHITE);
            if (selectedPosition == i)
                textView.setBackgroundResource(R.drawable.id_mode_select_bg);
            return convertView;
        }
    }

    private OnSpinnerListener spinnerListener;

    public void setOnSpinnerListener(OnSpinnerListener listener) {
        spinnerListener = listener;
    }

    public interface OnSpinnerListener {
        void onItemClick(int position);
        void onDismiss();
    }
}
