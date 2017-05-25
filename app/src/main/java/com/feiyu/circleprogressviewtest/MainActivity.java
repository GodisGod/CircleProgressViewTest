package com.feiyu.circleprogressviewtest;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feiyu.circleprogressviewtest.recorder.MediaManager;
import com.feiyu.circleprogressviewtest.recorder.RecordView;

public class MainActivity extends AppCompatActivity {

    //控件初始化
    private HDCircleProgressView2 progressView;
    private LinearLayout lineBottom;
    private RecordView imgStart;
    private ImageView imgPlay;
    private ImageView imgRestart;
    private TextView tvTimeTip;
    private TextView tvTip;

    //进度
    private int progress = 0;
    //最大录制时间
    private int MAXTIME = 90 * 10;

    final Handler handler = new Handler();

    Runnable task = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 100);
            progress++;
            progressView.setProgressNotInUiThread(progress);

            if (isPlaying) {
                tvTimeTip.setText(progress / 10 + "s");
            } else {
                tvTimeTip.setText(90 - progress / 10 + "s");
            }

            if (progress == recordTime) {
                handler.removeCallbacksAndMessages(null);
                tvTip.setText("点击播放 重新录制");
            }

        }
    };

    //是否在录制
    private boolean isRecord = false;
    //是否在播放
    private boolean isPlaying = false;
    //录音文件的路径
    private String soundPath;
    //播放时间
    private int recordTime = 0;
    //是否点击了录制按钮
    private boolean isClick = false;
    //录音时间是否过短
    private boolean isTooShort = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();

    }

    private void initView() {
        lineBottom = (LinearLayout) findViewById(R.id.line_play_restart);
        imgPlay = (ImageView) findViewById(R.id.img_play);
        imgStart = (RecordView) findViewById(R.id.img_start);
        imgRestart = (ImageView) findViewById(R.id.img_restart);
        tvTimeTip = (TextView) findViewById(R.id.tv_time_tip);
        tvTip = (TextView) findViewById(R.id.tv_tip);

        tvTip.setText("点击话筒 开始录音");

        progressView = (HDCircleProgressView2) findViewById(R.id.circleProgressbar);
        progressView.setmMaxProgress(MAXTIME);
        progressView.setmProgress(0);

        imgStart.setMaxVoice(16);
        imgStart.setOnAudioRecordListener(new RecordView.AudioRecordListener() {
            @Override
            public void hasRecord(int seconds) {
                Log.i("LHD", "hasrecord: " + seconds);
                progressView.setProgressNotInUiThread(seconds);
            }

            @Override
            public void finish(int seconds, String filePath) {
                Log.i("LHD", "finish: " + seconds / 10 + "  " + filePath);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTip.setText("试听录音 重新录制");
                    }
                });
                isTooShort = false;
                imgStart.setVisibility(View.GONE);
                imgPlay.setVisibility(View.VISIBLE);
                lineBottom.setVisibility(View.VISIBLE);
                soundPath = filePath;
                recordTime = seconds;
                Log.i("LHD", "录制时间(100ms): " + recordTime);
            }

            @Override
            public void tooShort() {
                Log.i("LHD", "tooShort: ");
                isTooShort = true;
            }

            @Override
            public void curVoice(int voice) {
                Log.i("LHD", "voice :  " + voice);
            }
        });

    }

    private void initEvent() {
        imgStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LHD", "isRecord: " + isRecord);
                isClick = true;
                if (!isRecord) {
                    isRecord = true;
                    tvTip.setText("再次点击 完成录音");
                    imgStart.start();
                    handler.post(task);
                } else {
                    isRecord = false;
                    imgStart.stop();
                    progress = 0;

                    if (isTooShort) {
                        tvTip.setText("时长太短");
                        //重新录制
                        imgPlay.setVisibility(View.GONE);
                    } else {
                        tvTip.setText("试听录音 重新录制");
                    }

                    lineBottom.setVisibility(View.VISIBLE);
                    imgStart.setVisibility(View.GONE);
                    handler.removeCallbacksAndMessages(null);
                }
            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    imgPlay.setImageResource(R.drawable.record_play);
                    isPlaying = false;
                    progress = 0;
                    handler.removeCallbacksAndMessages(null);
                    MediaManager.release();
                } else {
                    imgPlay.setImageResource(R.drawable.record_pause);
                    isPlaying = true;
                    tvTip.setText("");
                    handler.post(task);
                    MediaManager.playSound(soundPath, new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            imgPlay.setImageResource(R.drawable.record_play);
                            isPlaying = false;
                            progress = 0;
                            handler.removeCallbacksAndMessages(null);
                        }
                    });
                }
            }
        });


        imgRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

    }

    private void reset() {
        //删除之前的录音文件

        //重新开始录制
        progress = 0;
        isRecord = false;
        isPlaying = false;
        imgPlay.setVisibility(View.VISIBLE);
        handler.removeCallbacksAndMessages(null);
        progressView.clear();
        tvTimeTip.setText("90s");
        tvTip.setText("点击话筒 开始录音");
        lineBottom.setVisibility(View.GONE);
        imgStart.setVisibility(View.VISIBLE);
        MediaManager.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("LHD", "onDestroy");
        handler.removeCallbacksAndMessages(null);
        MediaManager.release();
        if (isClick) {//只有调用过了imgStart.start()以后才可以使用stop方法
            imgStart.stop();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //重新开始录制
        finish();
    }


}
