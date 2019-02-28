package com.talkfun.cloudlive.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.sdk.module.VoteOption;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VoteAdapter extends BAdapter<VoteOption> {
    //    private LayoutInflater layoutInflater;
//    private List<VoteOption> optionList = new ArrayList<>();
//    private Context mContext;
    private final int SINGLE = 1;
    private final int MULTIPLE = 2;
    private int checkboxStyle = SINGLE;
    private char[] choice = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private boolean isAllItemEnable = true;

    public VoteAdapter(Context context) {
        super(context);
    }

//    public void setOptionList(List<VoteOption> optionList) {
//        this.optionList = optionList;
//        notifyDataSetChanged();
//    }

    public void setCheckboxStatus(int pos) {
        itemList.get(pos).setIsSelected(true);
        notifyDataSetChanged();
    }

    public void setCheckboxStyle(boolean style) {
        checkboxStyle = style ? SINGLE : MULTIPLE;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = loadView(R.layout.ht_vote_list_item_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        VoteOption option = itemList.get(position);
        switch (checkboxStyle) {
            case SINGLE:
                viewHolder.itemCb.setVisibility(View.GONE);
                viewHolder.singleItemCb.setVisibility(View.VISIBLE);
                viewHolder.singleItemCb.setChecked(option.isSelected());
                break;
            case MULTIPLE:
                viewHolder.itemCb.setVisibility(View.VISIBLE);
                viewHolder.itemCb.setChecked(option.isSelected());
                viewHolder.singleItemCb.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        viewHolder.itemContent.setText(option.getContent());
        if (position <= choice.length)
            viewHolder.choice.setText(choice[position] + "");

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return isAllItemEnable;
    }

    @Override
    public boolean isEnabled(int position) {
        return isAllItemEnable;
    }

    public void disableAllItemChoser() {
        isAllItemEnable = false;
        notifyDataSetChanged();
    }
    public void enableItemChoser() {
        isAllItemEnable = true;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @BindView(R2.id.choice_item)
        CheckBox itemCb;
        @BindView(R2.id.single_choice_item)
        CheckBox singleItemCb;
        @BindView(R2.id.choiceTv)
        TextView choice;
        @BindView(R2.id.choice_content)
        TextView itemContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}