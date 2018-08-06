package com.example.aiiage.myhandlethread.EventBusSticky;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.aiiage.myhandlethread.R;
import com.example.aiiage.myhandlethread.RxJavaDemo.RxJavaDemoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.content.ContentValues.TAG;

public class FirstStickyActivity extends Activity {
    private TextView show_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_sticky);
        show_message=findViewById(R.id.tv_sticky);
        show_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FirstStickyActivity.this, RxJavaDemoActivity.class);
                startActivity(intent);
            }
        });
        /*注册*/
        EventBus.getDefault().register(FirstStickyActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除所有的粘性事件
        EventBus.getDefault().removeAllStickyEvents();
        //解注册
        EventBus.getDefault().unregister(FirstStickyActivity.this);
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void getStickyMessageEvent(EventBusMsg msg){
        Log.d(TAG,"接收信息");
        show_message.setText(msg.name);
    }
}
