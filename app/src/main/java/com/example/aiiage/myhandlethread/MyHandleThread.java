package com.example.aiiage.myhandlethread;

import android.app.Application;
import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

public class MyHandleThread extends Application{
    //private static Context mcontext;
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化Bugly
        CrashReport.initCrashReport(getApplicationContext(), "b890937167", false);
    }
}
