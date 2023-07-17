package com.coffee.zdevsuper.ui.dialog

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.SurfaceTexture
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.coffee.base.manager.AudioFocusMgr
import com.coffee.base.ui.BaseDialog
import com.coffee.base.utils.LogUtil
import com.coffee.zdevsuper.R
import com.coffee.zdevsuper.databinding.DialogGuideVideoBinding
import kotlinx.coroutines.*

/**
 *
 * @Description: 视频引导弹框
 * @Author: ly-zfensheng
 * @CreateDate: 2022/11/14 15:16
 *
 * //TODO: 后面在构造方法中增加一个视频资源路径的参数
 */
class GuideVideoDialog(context: Context, private val isSystemAlert: Boolean, private val lifecycleScope: CoroutineScope?)
    : BaseDialog<DialogGuideVideoBinding>(context), View.OnClickListener,
    TextureView.SurfaceTextureListener {

    private var surfaceAvailableTask: Job? = null

    private var combineProgressTask: Job? = null

    private var surface: Surface? = null
    private var mediaPlayer: MediaPlayer? = null

    private var mAudioAttributes: AudioAttributes? = null

    private lateinit var audioFocusMgr: AudioFocusMgr

    override val defineSystemAlertType: Boolean
        get() = isSystemAlert

    override val defineFullScreen: Boolean
        get() = false

    //沉浸式隐藏动画
    private val fadeAnimator: ValueAnimator = ValueAnimator.ofFloat(1f, 0f).also {
        it.duration = 500
        it.addUpdateListener {
            val value = it.animatedValue as Float
            mBinding.apply {
                title.alpha = value
                playOrPause.alpha = value
                currentPos.alpha = value
                seekBar.alpha = value
                totalPos.alpha = value
                if (value == 0f) {
                    title.visibility = View.INVISIBLE
                    playOrPause.visibility = View.INVISIBLE
                    currentPos.visibility = View.INVISIBLE
                    seekBar.visibility = View.INVISIBLE
                    totalPos.visibility = View.INVISIBLE
                }
            }
        }
    }

    //沉浸式显示动画
    private val showAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).also {
        it.duration = 500
        it.addUpdateListener {
            val value = it.animatedValue as Float
            mBinding.apply {
                if (value == 0f) {
                    title.visibility = View.VISIBLE
                    playOrPause.visibility = View.VISIBLE
                    currentPos.visibility = View.VISIBLE
                    seekBar.visibility = View.VISIBLE
                    totalPos.visibility = View.VISIBLE
                }
                title.alpha = value
                playOrPause.alpha = value
                currentPos.alpha = value
                seekBar.alpha = value
                totalPos.alpha = value
            }
        }
    }

    private val seekBarChangedListener: SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            // 取得当前进度条的刻度
            val progress = seekBar.progress
            mediaPlayer?.seekTo(progress * 1000)
        }
    }

    override fun initView(binding: DialogGuideVideoBinding) {
        initAudio()
        binding.apply {
            close.setOnClickListener(this@GuideVideoDialog)

            //播放或暂停按钮监听
            playOrPause.setOnClickListener(this@GuideVideoDialog)

            //进度条设置进度监听
            seekBar.setOnSeekBarChangeListener(seekBarChangedListener)

            mediaPlayer = MediaPlayer()
            mediaPlayer?.setOnPreparedListener { mp ->
//                mp.start()
                val tempTureView = mBinding.textureView
                val ratio: Float = mp.videoWidth * 1f / mp.videoHeight
                val height: Int = tempTureView.height
                val layoutParams: ViewGroup.LayoutParams = tempTureView.layoutParams
                layoutParams.width = (height * ratio).toInt()
                tempTureView.layoutParams = layoutParams

                currentPos.text = generateTime(mp.currentPosition.toLong())
                totalPos.text = generateTime(mp.duration.toLong())
                val maxProgress = mp.duration / 1000
                LogUtil.d("总的秒数: $maxProgress")
                seekBar.max = maxProgress

                //播放视频
                playOrPause.performClick()
            }
            mediaPlayer?.setOnCompletionListener {
                LogUtil.d("视频播放完成")
                //视频资源回到初始时间位置
                it.seekTo(0)
                //暂停播放
                it.pause()
                //其它控件更新为初始状态
                playOrPause.isSelected = false
                mBinding.currentPos.text = generateTime(it.currentPosition.toLong())
                mBinding.seekBar.progress = it.currentPosition / 1000
                stopProgressTask()
            }
            mediaPlayer?.setOnErrorListener { mp, what, extra ->
                LogUtil.d("视频发生错误")
                return@setOnErrorListener false
            }
            binding.textureView.setOnClickListener(this@GuideVideoDialog)
            binding.textureView.surfaceTextureListener = this@GuideVideoDialog
        }
    }

    private fun initAudio() {
        audioFocusMgr = AudioFocusMgr(context)
        mAudioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE).build()
    }

    fun setTitleText(titleStr: String) {
        mBinding.title.text = titleStr
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestFocusAsync(): Deferred<Boolean> {
        val response = CompletableDeferred<Boolean>()
        if (audioFocusMgr.isFocusGained.not()) {
            audioFocusMgr.request(mAudioAttributes, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK, object: AudioFocusMgr.IAudioFocusChangedListener {
                override fun onLost() {
                    LogUtil.d("audio focus on lost")
                    response.complete(false)
                }

                override fun onGained() {
                    LogUtil.d("audio focus on gained")
                    response.complete(true)
                }

                override fun onFailed() {
                    LogUtil.d("audio focus on failed")
                    response.complete(false)
                }
            })
        } else {
            LogUtil.d("audio focus had bean gained")
            response.complete(true)
        }
        return response
    }

    override fun initData() {
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.play_or_pause -> {
                mBinding.apply {
                    if (mediaPlayer?.isPlaying == true) {
                        //暂停逻辑
                        mediaPlayer?.pause()
                        view.isSelected = false
                        stopProgressTask()
                    } else {
                        //播放逻辑
                        mediaPlayer?.start()
                        view.isSelected = true
                        startProgressTask()
                    }
                }
            }
            R.id.close -> dismiss()

            R.id.textureView -> {
                //沉浸式处理
                mBinding.apply {
                    if (seekBar.isVisible) {
                        if (fadeAnimator.isRunning.not() && showAnimator.isRunning.not()) {
                            fadeAnimator.start()
                        }
                    } else {
                        if (showAnimator.isRunning.not()) {
                            showAnimator.start()
                        }
                    }
                }
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
//        stopMediaPlayer()
        stopProgressTask()
        if (fadeAnimator.isRunning) {
            fadeAnimator.cancel()
        }
        if (showAnimator.isRunning) {
            showAnimator.cancel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun stopMediaPlayer() {
        if (surfaceAvailableTask?.isActive == null) {
            surfaceAvailableTask?.cancel()
            surfaceAvailableTask = null
        }
        //释放资源
        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        mediaPlayer = null

        audioFocusMgr.abandon()
    }

    private fun startMediaPlayer() {
        val uri = Uri.parse("android.resource://${context.packageName}/${R.raw.loveandpeace}")
        mediaPlayer?.let {
            it.setDataSource(context, uri)
            it.setSurface(surface)
            it.setAudioStreamType(AudioManager.STREAM_MUSIC)
            it.isLooping = false
            it.prepareAsync()
        }
    }

    private fun startProgressTask() {
        combineProgressTask = lifecycleScope?.launch(Dispatchers.Default) {
            while (isActive) {
                if (mediaPlayer?.isPlaying == true) {
                    val currentPosition = mediaPlayer!!.currentPosition
                    val progress = currentPosition / 1000
                    val currentTimeString = generateTime(currentPosition.toLong())
                    withContext(Dispatchers.Main.immediate) {
                        //进度条更新进度
                        mBinding.seekBar.progress = progress
                        //进度文案更新
                        mBinding.currentPos.text = currentTimeString
                    }
                }
            }
        }
    }

    private fun stopProgressTask() {
        combineProgressTask?.cancel()
        combineProgressTask = null
    }

    private fun generateTime(time: Long): String {
        val totalSeconds = (time / 1000).toInt()
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        surfaceAvailableTask = lifecycleScope?.launch(Dispatchers.Default) {
            if (requestFocusAsync().await()) {
                surface = Surface(surfaceTexture)
                startMediaPlayer()
            } else {
                ToastUtils.showShort("视频资源出错")
            }
        }
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        surface = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopMediaPlayer()
        }
        return true
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
    }

}