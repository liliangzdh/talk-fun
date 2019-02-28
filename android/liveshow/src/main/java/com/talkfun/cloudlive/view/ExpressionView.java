package com.talkfun.cloudlive.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.adapter.ViewPagerAdapter;
import com.talkfun.cloudlive.entity.ExpressionEntity;
import com.talkfun.cloudlive.event.OnExpressionListener;
import com.talkfun.cloudlive.module.ExpressionData;
import com.talkfun.cloudlive.adapter.ExpressionAdapter;
import com.talkfun.cloudlive.util.DimensionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class ExpressionView extends FrameLayout implements AdapterView.OnItemClickListener {

    private Context mContext;
    private ViewPager mPagerContainer;
    /**
     * 表情适配器列表
     */
    private List<ExpressionAdapter> mEmoAdapters;
    private List<View> mPageViews;
    /**
     * 每页列数
     */
    private int mPageColumn = 7;
    private int padding;
    private ExpressionData mDataAction;
    /**
     * 当前页
     */
    private int mCurrentPage;

    private String[] expressionChars = {"[aha]", "[hard]", "[good]", "[love]", "[flower]", "[cool]", "[why]", "[pitiful]", "[amaz]", "[bye]"};
    private int[] expressionResIds = {R.mipmap.aha, R.mipmap.hard, R.mipmap.good, R.mipmap.love, R.mipmap.flower,
            R.mipmap.cool, R.mipmap.why, R.mipmap.pitiful, R.mipmap.amaz, R.mipmap.bye};


    public ExpressionView(Context context, int pageColumn) {
        super(context);
        mPageColumn = pageColumn;
        init(null, 0);
    }

    public ExpressionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ExpressionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        try {
            mContext = this.getContext();
            padding = DimensionUtils.dip2px(mContext, 5);
            mDataAction = new ExpressionData();
            mDataAction.parseData(expressionChars, expressionResIds);
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        initViews();
        initViewPager();
    }

    /**
     * 初始化iewPager
     */
    private void initViewPager() {
        mPagerContainer = new ViewPager(mContext);
        this.addView(mPagerContainer);
        ViewPagerAdapter pageAdapeter = new ViewPagerAdapter(mPageViews);
        mPagerContainer.setAdapter(pageAdapeter);
        mPagerContainer.setCurrentItem(0);
        mPagerContainer.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCurrentPage = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /***
     * 初始化表情GridView
     */
    private void initViews() {
        if (mEmoAdapters == null)
            mEmoAdapters = new ArrayList<ExpressionAdapter>();
        if (mPageViews == null)
            mPageViews = new ArrayList<View>();
        GridView gridView;
        ExpressionAdapter emoAdapter;
        for (int i = 0, len = mDataAction.getPageEmoEntitys().size(); i < len; i++) {
            gridView = new GridView(mContext);
            emoAdapter = new ExpressionAdapter(mContext, mPageColumn);
            emoAdapter.setItems(mDataAction.getPageEmoEntitys().get(i));
            gridView.setAdapter(emoAdapter);
            mEmoAdapters.add(emoAdapter);

            gridView.setNumColumns(mPageColumn);
            gridView.setBackgroundColor(Color.TRANSPARENT);
            gridView.setHorizontalSpacing(1);
            gridView.setVerticalSpacing(1);
            gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            gridView.setCacheColorHint(0);
            gridView.setPadding(padding, padding, padding, padding);
            gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            gridView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            gridView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
            gridView.setOnItemClickListener(this);
            gridView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mPageViews.add(gridView);
        }
    }

    private OnExpressionListener mListener;

    /**
     * 设置选中表情监听事件回调接口
     */
    public void setOnEmotionSelectedListener(OnExpressionListener listener) {
        mListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ExpressionEntity entry = (ExpressionEntity) mEmoAdapters.get(mCurrentPage).getItem(position);
        if (entry != null && !TextUtils.isEmpty(entry.character)) {

            if (mListener != null) {
                if (entry.resId == R.mipmap.iv_delete_expression) {
                    mListener.OnExpressionRemove();
                } else {
                    mListener.OnExpressionSelected(entry);
                }
            }
        }
    }
}
