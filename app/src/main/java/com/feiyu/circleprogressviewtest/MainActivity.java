package com.feiyu.circleprogressviewtest;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.feiyu.circleprogressviewtest.recorder.MediaManager;
import com.feiyu.circleprogressviewtest.recorder.RecordView;

public class MainActivity extends AppCompatActivity {

    private HDCircleProgressView progressView;
    private int progress = 0;


    private int MAXTIME = 90 * 10;

    final Handler handler = new Handler();

    Runnable task = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 100);
            progress++;
            progressView.setProgressNotInUiThread(progress);

            if (progress == recordTime) {
                handler.removeCallbacksAndMessages(null);
                progressView.setmTxtHint1("点击播放 重新录制");
            }

        }
    };

    private boolean isRecord = false;

    private LinearLayout lineBottom;
    private RecordView imgStart;
    private ImageView imgPlay;
    private ImageView imgRestart;

    private boolean isPlaying = false;
    private String soundPath;

    private int recordTime = 0;
    private boolean isClick = false;

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

        progressView = (HDCircleProgressView) findViewById(R.id.circleProgressbar);
        progressView.setmProgress(0);
        progressView.setmTxtHint1("点击话筒 开始录音");

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
                progressView.setmTxtHint1("点击播放 重新录制");
                progressView.invalidate();
                imgStart.setVisibility(View.GONE);
                lineBottom.setVisibility(View.VISIBLE);
                soundPath = filePath;
                recordTime = seconds;
            }

            @Override
            public void tooShort() {
                Log.i("LHD", "tooShort: ");
                progressView.setmTxtHint1("时长太短");
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
                    progressView.setmTxtHint1("再次点击 完成录音");
                    imgStart.start();
                    handler.post(task);
                } else {
                    isRecord = false;
                    imgStart.stop();
                    progress = 0;
                    progressView.setmTxtHint1("试听录音 重新录制");
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
                    MediaManager.playSound(soundPath, new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            imgPlay.setImageResource(R.drawable.record_play);
                            isPlaying = false;
                            progress = 0;
                            handler.removeCallbacksAndMessages(null);
                        }
                    });
                    handler.post(task);
                }
            }
        });


        imgRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除之前的录音文件

                //重新开始录制
                progress = 0;
                isRecord = false;
                isPlaying = false;
                handler.removeCallbacksAndMessages(null);
                progressView.clear();
                progressView.setmTxtHint1("点击话筒 开始录音");
                progressView.invalidate();
                lineBottom.setVisibility(View.GONE);
                imgStart.setVisibility(View.VISIBLE);
                MediaManager.release();
            }
        });

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
