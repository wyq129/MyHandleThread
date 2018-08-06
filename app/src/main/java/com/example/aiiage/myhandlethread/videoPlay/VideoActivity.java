package com.example.aiiage.myhandlethread.videoPlay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.aiiage.myhandlethread.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends Activity implements View.OnClickListener {
    @BindView(R.id.video_back)
    ImageView back;
    @BindView(R.id.iv_video_play)
    ImageView iv_video_play;
    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.videoView_intro)
    TextView videoView_intro;
    final String  path= "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    final String  path1= "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    final String  path2= "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    final String  path3= "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    final String  path4= "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    final String  path5= "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    final String text="这是第一个视频";
    final String text1="这是第二个视频";
    final String text2="这是第三个视频";
    final String text3="这是第四个视频";
    final String text4="这是第五个视频";
    final String text5="这是第六个视频";
    Context context;
    String intro;
    private List videos = new ArrayList<>();
    private List showText=new ArrayList();
    String[] arry={path,path1,path2,path3,path4,path5};
    String[] show_text={text,text1,text2,text3,text4,text5};
    int curSongIndex;
    int next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        context=VideoActivity.this;
        ButterKnife.bind(VideoActivity.this);
        videoView_intro.setVisibility(View.GONE);
        iv_video_play.setVisibility(View.GONE);
        //   获取MediaController对象，控制媒体播放，这里应该是获取 android.widget.MediaController 的对象
        MediaController mediaController = new MediaController(context);

        //去除进度条
        mediaController.setVisibility(View.INVISIBLE);
        //  绑定到 Video View
        videoView.setMediaController(mediaController);
        curSongIndex = getIntent().getIntExtra("position", 0);
        getVideo();
        //播放完成回调
        videoView.setOnCompletionListener( new MyPlayerOnCompletionListener());

        videoView.setVideoPath(path);

        back.setOnClickListener(this);
        iv_video_play.setOnClickListener(this);
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (videoView.isPlaying()){
                    iv_video_play.setVisibility(View.VISIBLE);
                    videoView.pause();

                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.video_back:
                finish();
                break;
            case R.id.iv_video_play:
                videoView.start();
                iv_video_play.setVisibility(View.GONE);
                break;
        }
    }
    private void playNextVideo() {
        curSongIndex++;
        if (curSongIndex >= arry.length) {
            videoView.stopPlayback();
            videoView_intro.setText("视频播完了");
        }
        else {
            getVideo();
        }

    }

    private void getVideo() {
        for (int i=0;i<arry.length;i++){
            videos.add(arry);
            showText.add(show_text);
            next=curSongIndex+1;
            videoView.setVideoPath(arry[curSongIndex]);
            videoView_intro.setVisibility(View.VISIBLE);
            videoView_intro.setText(show_text[curSongIndex]);
            videoView.start();
            Toast.makeText(context, "即将播放第"+next+"个视频", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (curSongIndex>=arry.length){
                mp.release();
                mp.stop();
            }
            playNextVideo();
        }
    }
}
