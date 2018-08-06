package com.example.aiiage.myhandlethread.OKGO;

import android.app.Activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.aiiage.myhandlethread.R;


import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.tencent.bugly.crashreport.CrashReport;


import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class OkGoActivity extends Activity {
    @BindView(R.id.btn)
     Button button;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_go);
        ButterKnife.bind(this);

        url="http://ww.baidu.com";
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              switch (view.getId()){
                  case R.id.btn:
                      getAction(url);
                      break;
              }

            }
        });

    }

    private void getAction(final String url) {
        OkGo.<String>get(url).tag(OkGoActivity.this).execute(new StringCallback()  {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Toast.makeText(OkGoActivity.this,url, Toast.LENGTH_SHORT).show();
                Uri uri=Uri.parse("http://www.baidu.com");
                //CrashReport.testJavaCrash();
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
    }

}
