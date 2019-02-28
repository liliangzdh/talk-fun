package com.talkfun.cloudlive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.imageload.GlideImageLoader;

import java.util.List;

/**
 * Created by ccy on 2017/11/17.
 */


/**
 * Created by shenmegui on 2017/7/6.
 */
public class AutoScrollAdapter extends RecyclerView.Adapter<AutoScrollAdapter.AskRequestHolder> {
    private List<Integer> products;
    private Context context;
    private LayoutInflater mLayoutInflater;

    public AutoScrollAdapter(List<Integer> products, Context context) {
        this.products = products;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public AskRequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = mLayoutInflater.inflate(R.layout.item_scroll, parent, false);
        AskRequestHolder askRequestHolder = new AskRequestHolder(inflate);
        return askRequestHolder;
    }

    @Override
    public void onBindViewHolder(AskRequestHolder holder, int position) {
        //无限循环
        position = position % products.size();
//        Glide.with(context).load(products.get(position)).into(holder.ico);
        GlideImageLoader.create(holder.ico).load(products.get(position));
//        Product product = products.get(position);
//        holder.ico.setImageBitmap(product.getBitmap());

    }


    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class AskRequestHolder extends RecyclerView.ViewHolder {
        private ImageView ico;

        public AskRequestHolder(View itemView) {
            super(itemView);
            ico = (ImageView) itemView.findViewById(R.id.iv_ico);
        }
    }
}