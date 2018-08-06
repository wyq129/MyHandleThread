package com.example.aiiage.myhandlethread;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.nio.Buffer;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerActivity extends Activity {
    @BindView(R.id.ijk_player)
    VideoPlayerIJK ijkPlayer;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_setting)
    Button btnSetting;
    @BindView(R.id.btn_play)
    Button btnPlay;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.btn_stop)
    Button btnStop;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_load_msg)
    TextView tvLoadMsg;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.rl_loading)
    RelativeLayout rlLoading;
    @BindView(R.id.tv_play_end)
    TextView tvPlayEnd;
    @BindView(R.id.rl_player)
    RelativeLayout rlPlayer;
private  String path;
private  boolean start=false;
private Handler handler;
public static final int MSG_REFRESH=1001;
private TimerTask timerTask;
private long time;
private Timer timer;

private RelativeLayout rl_top,rl_buttom;
private boolean menu_visible= true;
private boolean orientation=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        rl_buttom=findViewById(R.id.include_play_buttom);
        rl_top=findViewById(R.id.include_play_top);
        path=getIntent().getExtras().getString("path","0");
        /*加载so文件*/
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        ijkPlayer=findViewById(R.id.ijk_player);
        ijkPlayer.setListener(new VideoPlayerListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

            }

            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                btnPlay.setText("播放");
            }

            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                return false;
            }

            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                iMediaPlayer.start();
                rlLoading.setVisibility(View.GONE);
            }

            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {

            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                /*开始拖动*/
                handler.removeCallbacksAndMessages(null);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                /*停止拖动*/
                ijkPlayer.seekTo(ijkPlayer.getDuration()*seekBar.getProgress() /100);
                handler.sendEmptyMessageAtTime(MSG_REFRESH,100);
            }
        });
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_REFRESH:
                        if (ijkPlayer.isPlaying()){
                            refresh();
                            handler.sendEmptyMessageDelayed(MSG_REFRESH, 1000);
                        }
                        break;
                }
            }
        };
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                long t = System.currentTimeMillis();
                if (t - time > 3000 && menu_visible) {
                    time = t;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_bottom);
                            rl_buttom.startAnimation(animation);
                            Animation animation_top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_top);
                            rl_top.startAnimation(animation_top);
                            menu_visible = false;


                        }
                    });
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setVisible(View.INVISIBLE);
                        }
                    }, 500);
                }


            }
        };

        timer.schedule(timerTask, 1000, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (start == false) {
            start = true;
            if (!path.equals("")) {

                loadVideo(path);
                handler.sendEmptyMessageDelayed(MSG_REFRESH, 1000);

            }

            time = System.currentTimeMillis();
        }
    }


    private void loadVideo(String path) {
        ijkPlayer.setVideoPath(path);
    }

    @Override
    protected void onStop() {
        IjkMediaPlayer.native_profileEnd();
        handler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (ijkPlayer != null) {
            ijkPlayer.stop();
            ijkPlayer.release();
            ijkPlayer = null;
        }
        super.onDestroy();
    }

    //正真的全屏，隐藏了状态栏、AtionBar、导航栏
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    @OnClick({R.id.ijk_player,R.id.btn_back,R.id.btn_setting,R.id.btn_play,R.id.btn_stop})
    public void onViewClicked(View v){
        switch (v.getId()){
            case R.id.ijk_player:
                if (menu_visible==false){
                    setVisible(View.VISIBLE);
                    Animation animation=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.show_bottom);
                    rl_buttom.startAnimation(animation);
                    Animation animation1=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.show_top);
                    rl_top.startAnimation(animation1);
                    menu_visible =true;
                    time=System.currentTimeMillis();
                }
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_play:
                if (btnPlay.getText().toString().equals(getResources().getString(R.string.pause))){
                    ijkPlayer.pause();
                    btnPlay.setText(getResources().getString(R.string.media_play));
                }else {
                    if (tvPlayEnd.getVisibility()==View.VISIBLE){
                        ijkPlayer.setVideoPath(path);
                    }
                    ijkPlayer.start();
                    tvPlayEnd.setVisibility(View.VISIBLE);
                    btnPlay.setText(getResources().getString(R.string.pause));
                }
                break;
            case R.id.btn_stop:
                btnPlay.setText(getResources().getString(R.string.media_play));
                ijkPlayer.stop();
                tvPlayEnd.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void setVisible(int invisible) {
        btnPlay.setVisibility(invisible);
        btnStop.setVisibility(invisible);
        btnBack.setVisibility(invisible);
        btnSetting.setVisibility(invisible);
        seekBar.setVisibility(invisible);
    }

    private void refresh() {
        long current = ijkPlayer.getCurrentPosition() / 1000;
        long duration = ijkPlayer.getDuration() / 1000;
        Log.v("zzw", current + " " + duration);
        long current_second = current % 60;
        long current_minute = current / 60;
        long total_second = duration % 60;
        long total_minute = duration / 60;
        String time = current_minute + ":" + current_second + "/" + total_minute + ":" + total_second;
        tvTime.setText(time);
        if (duration != 0) {
            seekBar.setProgress((int) (current * 100 / duration));
        }
    }
}
