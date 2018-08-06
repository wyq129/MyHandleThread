package com.example.aiiage.myhandlethread;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aiiage.myhandlethread.videoPlay.VideoActivity;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyPlayerActivity extends AppCompatActivity {
    private static final String TAG=MyPlayerActivity.class.getSimpleName();
    @BindView(R.id.btn_play_localVideo)
    Button play_localVideo;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.btn_play_netVideo)
    Button play_netVideo;
    @BindView(R.id.tv_net_url)
    TextView tv_net_url;
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_player);
        /* 绑定初始化ButterKnife  */
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},5);
        }
        path= "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
        tv.setText(path);
    }
    @OnClick({R.id.btn_play_netVideo,R.id.btn_play_localVideo})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.btn_play_netVideo:
                Intent intent=new Intent(MyPlayerActivity.this,PlayerActivity.class);
                if (!TextUtils.isEmpty(path))
                    intent.putExtra("path",tv_net_url.getText().toString());
                startActivity(intent);
                break;
            case R.id.btn_play_localVideo:
                Toast.makeText(this, "出错了", Toast.LENGTH_SHORT).show();
              /*  Intent intent1=new Intent(MyPlayerActivity.this, VideoActivity.class);
                startActivity(intent1);*/
                break;
        }
    }

}
