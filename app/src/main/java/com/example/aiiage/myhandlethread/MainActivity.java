package com.example.aiiage.myhandlethread;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aiiage.myhandlethread.EnventBus.EnventBusActivity;
import com.example.aiiage.myhandlethread.EventBusSticky.StickyActivity;
import com.example.aiiage.myhandlethread.OKGO.OkGoActivity;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends FragmentActivity {
    private TextView textView;
    private Button button1,button2,button3,btn_intent;
    Context context;
    Handler mainHandler,workHandler;
    HandlerThread handlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        context=MainActivity.this;
        textView=findViewById(R.id.test_output);
        button1=findViewById(R.id.btn_post1);
        button2=findViewById(R.id.btn_post2);
        button3=findViewById(R.id.btn_post3);
        btn_intent=findViewById(R.id.btn_intent);
        findViewById(R.id.btn_getsticky).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, StickyActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_okgo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent=new Intent(context,OkGoActivity.class);
                    startActivity(intent);
            }
        });
        findViewById(R.id.btn_eventbus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, EnventBusActivity.class);
                startActivity(intent);
            }
        });
        btn_intent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,MyPlayerActivity.class);
                startActivity(intent);
            }
        });
        // 创建与主线程关联的Handler
        mainHandler=new Handler();
        /*步骤一：创建HandlerThread实例对象
        * 传入参数=线程名称，作用=标记改线程*/
        handlerThread=new HandlerThread("mainHandler");

        /*步骤二：启动线程*/
        handlerThread.start();
        /*
        * 步骤三：创建工作线程Handler，并且复写handlerMessage()
        * 作用：关联HandlerThread的Looper对象、实现消息的处理操作，并且与其他线程进行通信
        * 注：消息处理的操作（HandlerMessage（））的执行线程=handlerThreadler所创建的工作线程中执行
        * */
        workHandler=new Handler(handlerThread.getLooper()){
            @Override
            /*消息处理的操作*/
            public void handleMessage(Message msg) {
                //设置两种消息处理操作，通过msg来进行识别
                switch (msg.what){
                    case 1:
                        /*延迟操作*/
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        /*通过主线程Handler.post方法进行在主线程的 UI更新操作.*/
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText("我爱学习");
                            }
                        });
                        break;
                    case 2:
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText("我不爱学习");
                            }
                        });
                        break;
                }
            }
        };
        /*步骤四:
        *使用工作线程Handler向工作线程的消息队列发送消息
        * 在工作线程中,当消息循环时取出对应消息并且在工作线程执行相关操作.
        * */
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*通过sendMessage()发送*/
                //1.定义要发送的消息
                Message message=Message.obtain();
                message.what=1;//消息的标识
                message.obj="A";//消息的存放
                //2.通过Handler发送消息到其绑定的消息队列
                workHandler.sendMessage(message);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //通过sendMessage发送
                //定义要发送的消息
                Message message=Message.obtain();
                message.what=2;
                message.obj="B";
                //2.通过Handler发送消息到其绑定的消息队列
                workHandler.sendMessage(message);
            }
        });
        /*
        * 点击Button3：退出消息循环*/
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerThread.quit();
            }
        });
    }
}
