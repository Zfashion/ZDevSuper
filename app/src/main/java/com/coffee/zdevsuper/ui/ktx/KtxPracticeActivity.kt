package com.coffee.zdevsuper.ui.ktx

import android.graphics.Color
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.underline
import com.blankj.utilcode.util.JsonUtils
import com.coffee.base.ui.BaseActivity
import com.coffee.zdevsuper.databinding.ActivityKtxPracticeBinding

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2023/2/16 10:07
 */
class KtxPracticeActivity: BaseActivity<ActivityKtxPracticeBinding>() {

    val dataString = "{\"data\":{\"setting\":{\"advancedGuideSwitch\":true,\"beginnerGuideSwitch\":true,\"creationTime\":1676594116953,\"defaultSpeaker\":\"叶子\",\"detailModeSwitch\":true,\"dialogTimeout\":30000,\"interactionCount\":0,\"languageMode\":\"普通话\",\"loginStatus\":1,\"maxSpeechTime\":0,\"nickname\":\"\",\"noSpeechTimeout\":0,\"oneShotSwitch\":false,\"promotionGuideSwitch\":true,\"seMode\":1,\"shortcutsSwitch\":true,\"ttsSpeaker\":\"豆豆\",\"userCenterId\":\"\",\"userId\":-1,\"verbalWakeup\":false,\"vpaSkin\":-1}},\"messageType\":\"request\",\"protocolId\":60033,\"requestAuthor\":\"com.dfl.vpa\",\"requestCode\":\"\",\"responseCode\":\"1677045947730\",\"versionName\":\"1.0\"}"

    override fun initView(vb: ActivityKtxPracticeBinding) {
        val guideWords = buildSpannedString {
            color(Color.GREEN) {
                append("你可以说：")
            }
            color(Color.YELLOW) {
                append("第一个、下一页、引导词")
            }
        }

        val data = JsonUtils.getString(dataString, "data")
        vb.spannableText.text = data

    }

    override fun initData() {

    }

}