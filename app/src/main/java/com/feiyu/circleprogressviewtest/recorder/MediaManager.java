package com.feiyu.circleprogressviewtest.recorder;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by 李鸿达 on 2017/1/29.
 */

public class MediaManager {
    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;

    public static void playSound(String scripAudio, MediaPlayer.OnCompletionListener onCompletionListener) {
        Log.i("LHD","正在播放的音频： " + scripAudio);
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
        try {
            mMediaPlayer.setDataSource(scripAudio);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static boolean isPlaying() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

}
