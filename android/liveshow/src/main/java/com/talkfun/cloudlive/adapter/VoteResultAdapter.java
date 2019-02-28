package com.talkfun.cloudlive.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.R2;
import com.talkfun.sdk.module.BriefVoteEntity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VoteResultAdapter extends BAdapter<BriefVoteEntity> {
    //    private List<BriefVoteEntity> briefVoteEntityList = new ArrayList<>();
//    private Context context;
//    private LayoutInflater layoutInflater;
    private char[] choice = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public VoteResultAdapter(Context context) {
        super(context);
//        layoutInflater = LayoutInflater.from(context);
//        this.context = context;
//        briefVoteEntityList = list;
    }

//    @Override
//    public int getCount() {
//        return itemList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return briefVoteEntityList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = loadView(R.layout.ht_vote_result_list_item_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        BriefVoteEntity briefVoteEntity = itemList.get(position);
        if (position <= choice.length)
            viewHolder.numTv.setText(choice[position] + "");
        String percent = briefVoteEntity.getOpNum() + "(" + briefVoteEntity.getPercent() + "%)";
        viewHolder.percentTv.setText(percent);
        viewHolder.votePb.setMax(100);
        viewHolder.votePb.setProgress(briefVoteEntity.getPercent());
        return convertView;
    }


    static class ViewHolder {
        @BindView(R2.id.num)
        TextView numTv;
        @BindView(R2.id.progress)
        ProgressBar votePb;
        @BindView(R2.id.percent)
        TextView percentTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
