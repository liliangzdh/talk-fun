package com.talkfun;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestActivity extends Activity {


    @BindView(R2.id.text)
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_text);
        ButterKnife.bind(this);

        textView.setText("zhishiashias");
    }


    @OnClick(R.id.text)
    public void show(View view) {

        textView.setText("asasasas");
        Toast.makeText(this, "hasas", Toast.LENGTH_LONG).show();
    }
}
