package com.talkfun.cloudlive.helper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.talkfun.cloudlive.R;
import com.talkfun.cloudlive.dialog.NetworkChoiceFragment;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.event.OnGetNetworkChoicesCallback;
import com.talkfun.sdk.event.OnSetNetworkCallback;
import com.talkfun.sdk.module.NetItem;
import com.talkfun.sdk.module.NetWorkEntity;

/**
 * Created by ccy on 2017/11/10.
 */

public class NetChoiseDiologHelper {
    private Context context;
    private NetworkChoiceFragment networkChoiceFragment;

    public NetChoiseDiologHelper(Context context) {
        this.context = context;
    }

    /**
     * 显示网络选择弹窗
     */
    public void showNetworkChoiceDialog() {
        //获取网络数据
        HtSdk.getInstance().getNetworkList(new OnGetNetworkChoicesCallback() {
            @Override
            public void onGetChoicesSuccess(NetWorkEntity netWorkEntity) {
                if(netWorkEntity == null && netWorkEntity.getCdnItems().size() == 0) {
                    return;
                }
                if(networkChoiceFragment == null){
                    networkChoiceFragment = new NetworkChoiceFragment();
                }else if(networkChoiceFragment.isAdded()){
                    networkChoiceFragment.dismissAllowingStateLoss();
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable(NetworkChoiceFragment.BUNDLE_DATA_KEY,netWorkEntity);
                networkChoiceFragment.setArguments(bundle);

                networkChoiceFragment.setOnSelectedListener(new NetworkChoiceFragment.OnSelectedListener() {
                    @Override
                    public void onSelected(int linePosition, NetItem item) {
                        HtSdk.getInstance().setNetwork(linePosition, item, new OnSetNetworkCallback() {
                            @Override
                            public void onSwitchSuccess() {
                                Toast.makeText(context, context.getResources().getString(R.string.switch_net_success), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSwitchError(String msg) {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                        }); //设置网络
                    }
                });
                if (!networkChoiceFragment.isVisible() && netWorkEntity != null && netWorkEntity.getCdnItems().size() != 0) {
                    networkChoiceFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "NetworkChoiceFragment");
                }
            }

            @Override
            public void onGetChoicesError(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void resetSelectPosition() {
        if (networkChoiceFragment == null)
            return;
        networkChoiceFragment.resetSelectPosition();

    }
}
