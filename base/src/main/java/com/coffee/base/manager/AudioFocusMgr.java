package com.coffee.base.manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import androidx.annotation.RequiresApi;

/**
 * 音频焦点管理实现
 *
 * @author hehr
 */
public class AudioFocusMgr implements AudioManager.OnAudioFocusChangeListener {


    private static final String TAG = "AudioFocusMgr";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private AudioManager mAudioManager ;
    private final ReentrantLock mLock = new ReentrantLock();
    private AudioFocusRequest mRequester;
    private IAudioFocusChangedListener mListener;

    public AudioFocusMgr(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }
    /**
     * 申请焦点
     *
     * @param gained     焦点类型 ,
     *                   暂停后台多媒体使用：AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE ,
     *                   混音使用：AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
     * @param attributes {@link AudioAttributes}
     * @return boolean
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean request(AudioAttributes attributes, int gained, IAudioFocusChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(" audio focus listener is null . ");
        }
        if (attributes == null) {
            throw new IllegalArgumentException(" audio attributes is null . ");
        }
        mListener = listener;
        Log.d(TAG, " ready request audio focus gained type: " + gained);

        if (isFocusGained()) {
            Log.w(TAG, " already holed audio focus , drop repeat request . ");
            return true;
        }

        try {
            return executor.submit(() -> {
                try {
                    mLock.lock();
                    boolean r;
                    r = request(attributes, gained);
                    Log.d(TAG, " request audio focus ret: " + r);
                    if (r) {
                        if (mListener != null) {
                            mListener.onGained();//获取到焦点
                        }
                    } else {
                        if (mListener != null) {
                            mListener.onFailed();//焦点申请失败
                        }
                    }
                    return r;
                } finally {
                    mLock.unlock();
                }

            }).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * inner request
     *
     * @param attributes {@link  AudioAttributes}
     * @param gained     audio request gain type
     * @return boolean
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean request(AudioAttributes attributes, int gained) {
        mRequester = new AudioFocusRequest
                .Builder(gained)
                .setAcceptsDelayedFocusGain(false)//不允许延迟获取焦点
                .setAudioAttributes(attributes)
                .setOnAudioFocusChangeListener(this)
                .build();
        int r = mAudioManager.requestAudioFocus(mRequester);
        Log.d(TAG, " audio focus request ret: " + r);
        switch (r) {
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                Log.e(TAG, " audio focus request failed. ");
                //焦点申请失败
                return false;
            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                //焦点申请成功
                Log.i(TAG, " audio focus request succeed. ");
                return true;
            default:
                throw new IllegalStateException("  unknown  request focus gained type :" + r);
        }
    }

    /**
     * 释放焦点
     *
     * @return boolean
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean abandon() {
        int gained = focusGained();
        Log.d(TAG, " gained type:" + gained);
        if (mRequester != null) {
            Log.d(TAG, " hold focus ,ready to abandon .");
            mAudioManager.abandonAudioFocusRequest(mRequester);
        } else {
            Log.d(TAG, " not hold audio focus , do not abandon . ");
        }
        mRequester = null;
        return true;
    }


    /**
     * 获取当前持有焦点类型
     *
     * @return int 焦点类型
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int focusGained() {
        if (mRequester == null) {
            return -1;
        } else {
            return mRequester.getFocusGain();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isFocusGained() {
        int type = focusGained();
        Log.d(TAG, " focus gained type:" + type);
        if (mRequester == null) {
            return false;
        }
        return focusGained() != -1 && focusGained() != AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    @Override
    public void onAudioFocusChange(int loss) {
        Log.d(TAG, " audio focus loss:" + loss);
        switch (loss) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mListener != null) {
                    mListener.onLost();
                }
                break;
            default:
                Log.e(TAG, " audio focus changed , not deal type: " + loss);
                break;
        }
    }

    /**
     * 焦点变化回调
     */
    public interface IAudioFocusChangedListener {

        /**
         * 焦点丢失
         */
        void onLost();

        /**
         * 获得焦点
         */
        void onGained();

        /**
         * 焦点申请失败
         */
        void onFailed();

    }

}
