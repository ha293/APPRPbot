package com.starway.starrobot.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;

import com.starway.starrobot.R;

/**
 * Created by iBelieve on 2018/5/4.
 */


public class VolumeUtil {

    public static int MAX_VOLUME_LEVEL = 15;
    public static int MIN_VOLUME_LEVEL = 3;
    public static int VOLUME_LEVEL = 3;

    private static VolumeUtil instance;
    private final SoundPool audio;
    private final int audio_res;

    private Context context;
    private AudioManager mAudioManager;
//    private Ringtone ringtone;

    public static synchronized VolumeUtil getInstance(Context context) {
        VolumeUtil volumeUtil;
        synchronized (VolumeUtil.class) {
            if (instance == null) {
                instance = new VolumeUtil(context);
                volumeUtil = instance;
            } else {
                volumeUtil = instance;
            }
        }
        return volumeUtil;
    }

    private VolumeUtil(Context context) {
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.context = context;

        audio = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        audio_res = audio.load(context, R.raw.tick, 1);

        MAX_VOLUME_LEVEL = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 音量加
     */
    public void plusVolume() {
        plusVolume(VOLUME_LEVEL);
    }

    /**
     * 音量减
     */
    public void minusVolume() {
        minusVolume(VOLUME_LEVEL);
    }

    /**
     * 音量加一个值
     *
     * @param volume
     */
    public void plusVolume(int volume) {
        setVolume2(getCurrentVolume() + volume);
    }

    /**
     * 音量减一个值
     *
     * @param volume
     */
    public void minusVolume(int volume) {
        setVolume2(getCurrentVolume() - volume);

    }

    /**
     * 设置最大音量
     */
    public void setMaxVolume() {
        setVolume(MAX_VOLUME_LEVEL);
    }

    /**
     * 设置最小音量
     */
    public void setMinVolume() {
        setVolume(MIN_VOLUME_LEVEL);
    }

    /**
     * 设置音量
     *
     * @param volume
     */
    public void setVolume(int volume) {
        if (volume <= MIN_VOLUME_LEVEL) {
            volume = MIN_VOLUME_LEVEL;
        } else if (volume >= MAX_VOLUME_LEVEL) {
            volume = MAX_VOLUME_LEVEL;
        }
        if (this.mAudioManager != null) {
            this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_VIBRATE);
        }
    }

    /**
     * 设置音量（不对音量进行限制）
     *
     * @param volume
     */
    public void setVolume2(int volume) {
        if (volume <= 0) {
            volume = 0;
        } else if (volume >= MAX_VOLUME_LEVEL) {
            volume = MAX_VOLUME_LEVEL;
        }
        if (volume != getCurrentVolume()) {
            if (this.mAudioManager != null) {
                this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_VIBRATE);
            }
        }
        audio.play(audio_res, 1f, 1f, 1, 0, 1f);
    }

    /**
     * 获取当前音量
     *
     * @return
     */
    public int getCurrentVolume() {
        if (this.mAudioManager != null) {
            return this.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return -1;
    }
}
