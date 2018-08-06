package com.example.aiiage.myhandlethread.EventBusSticky;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.aiiage.myhandlethread.R;

import org.greenrobot.eventbus.EventBus;

public class StickyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky);
        findViewById(R.id.tv_sticky).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendStickyMessage(view);
            }
        });
    }


    public void sendStickyMessage(View view){
        //发送消息
        EventBus.getDefault().postSticky(new EventBusMsg("我是Sticky消息"));
        Intent intent=new Intent(StickyActivity.this,FirstStickyActivity.class);
        StickyActivity.this.startActivity(intent);
    }
}
