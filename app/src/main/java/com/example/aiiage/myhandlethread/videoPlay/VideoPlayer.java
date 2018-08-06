package com.example.aiiage.myhandlethread.videoPlay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.HashMap;

import static java.lang.Thread.sleep;

/**
 * Created by zia on 2017/5/20.
 */

public class VideoPlayer {

    private static final String TAG = "VideoPlayerTest";
    public MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SeekBar seekBar;
    private String url;
    private Context context;
    private ImageView item_Thumbnail;
    public int currentPosition = 0;//设置成外部可控制，以便从上次继续播放
    public int totleTime = 0;
    public boolean isPlaying = false;//主要用来控制进度条进度
    private boolean haveSeek = false;
    private ProgressListener listener;
    private boolean isAutoHeight = false;//设置自适应屏幕

    public interface ProgressListener {
        void getProgress(int currentPosition, int totalPosition);
    }

    public interface GetHeightListener {
        void getFixedSize(float height, float width);
    }

    //控制holder的生命周期
    private SurfaceHolder.Callback holderCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.i(TAG, "SurfaceHolder 被创建");
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.i(TAG, "SurfaceHolder 适应屏幕大小");
            Log.i(TAG, "i1:  " + i1 + "  i2:  " + i2);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.i(TAG, "SurfaceHolder 被销毁");
            stop();
        }
    };
    //监听进度条
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(seekBar.getProgress());
                isPlaying = true;
            }
        }
    };

    //构造方法
    public VideoPlayer(Context context, SurfaceView surfaceView, SeekBar seekBar) {
        this.surfaceView = surfaceView;
        this.context = context;
        this.seekBar = seekBar;
        setListener();
    }

    //设置自适应surfaceView高度，宽度由xml决定
    public void setAutoHeight() {
        isAutoHeight = true;
            getAutoHeight(new GetHeightListener() {
                @Override
                public void getFixedSize(float height, final float width) {
                    final ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
                    lp.height = (int) height;
                    lp.width = (int) width;
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            surfaceView.setLayoutParams(lp);
                        }
                    });
                }
            });

    }


    //获取视频按比例缩放的高度
    private void getAutoHeight(final GetHeightListener l) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(url, new HashMap<String, String>());
                    Bitmap bitmap = mmr.getFrameAtTime();
                    float w = bitmap.getWidth();
                    float h = bitmap.getHeight();
                    float w1 = surfaceView.getWidth();
                    l.getFixedSize(h / w * w1, w1);
                    mmr.release();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置进度条监听，这个回调可以获取视频总长度和当前位置
    public void setProgressListener(ProgressListener listener) {
        this.listener = listener;
    }

    //取视频第一帧作为缩略图显示
    public void setVideoThumbnail(final ImageView placeHolder) {
        try {
            this.item_Thumbnail = placeHolder;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(url, new HashMap<String, String>());
                    final Bitmap bitmap = mmr.getFrameAtTime();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                placeHolder.setImageBitmap(bitmap);
                            }
                        }
                    });
                    mmr.release();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //设置视频来源，必填
    public void setDataSource(String url) {
        this.url = url;
    }

    private void setListener() {
        surfaceView.getHolder().addCallback(holderCallBack);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    /**
     * 开一个新线程控制进度条，周期为0.5秒
     */
    private void setSeek() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run on Thread");
                while (true) {
                    if (isPlaying) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        if (listener != null) {
                            listener.getProgress(currentPosition, totleTime);
                        }
                        seekBar.setProgress(currentPosition);
                    }
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //播放
    public void play(final int position) {
        if (url == null) return;
        if (item_Thumbnail != null)
            item_Thumbnail.setVisibility(View.INVISIBLE);
        if (mediaPlayer == null) {//判断，如果已经有视频就不再
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(url);
                if(isAutoHeight) setAutoHeight();
                isPlaying = true;
                Log.i(TAG, "开始装载");
                mediaPlayer.prepareAsync();
                //缓存完的回调
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mediaPlayer) {
                        Log.i(TAG, "装载完成");
                        mediaPlayer.setDisplay(surfaceView.getHolder());
                        mediaPlayer.start();
                        mediaPlayer.seekTo(position);//进度重置
                        totleTime = mediaPlayer.getDuration();
                        seekBar.setMax(totleTime);
                        Log.d(TAG, "play");
                        if (!haveSeek) {
                            setSeek();
                            haveSeek = true;
                        }
                    }
                });
                //播放完后的回调
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Log.d(TAG, "播放完毕！");
                        pause();
                    }
                });
                //错误回调
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        Log.d(TAG, "播放错误！");
                        stop();
                        return false;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //重新播放
    public void replay() {
        if (mediaPlayer != null) {
            stop();
            play(currentPosition);
        }
    }

    //暂停以及继续，都是这个
    public void pause() {
        if (mediaPlayer != null && isPlaying) {
            Log.d(TAG, "pause!");
            mediaPlayer.pause();
            isPlaying = false;
            return;
        }
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
        }

    }

    //终止
    public void stop() {
        if (mediaPlayer != null) {
            Log.d(TAG, "stop!");
            isPlaying = false;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
