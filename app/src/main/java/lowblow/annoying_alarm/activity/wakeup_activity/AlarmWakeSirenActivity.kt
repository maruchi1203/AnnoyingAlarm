package lowblow.annoying_alarm.activity.wakeup_activity

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lowblow.annoying_alarm.R
import lowblow.annoying_alarm.data.alarm.AlarmEntity
import lowblow.annoying_alarm.databinding.AlarmWakeSirenBinding
import lowblow.annoying_alarm.system_manager.DataController
import java.util.*
import kotlin.properties.Delegates

class AlarmWakeSirenActivity : AppCompatActivity() {

    private val binding by lazy {
        AlarmWakeSirenBinding.inflate(layoutInflater)
    }
    private var releaseMode = false

    private lateinit var media: MediaPlayer

    private val vibrate by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    private lateinit var alarmEntity: AlarmEntity

    private var amount by Delegates.notNull<Int>()

    private val answer = mutableListOf<Int>()
    private val randomNumList = mutableListOf<Int>()            // Answer for releasing alarm
    private val buttonList = mutableListOf<CheckBox>()   // Group of buttons

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        media = MediaPlayer()
        val id = intent.getLongExtra("id", 0)

        initWakeLock()

        CoroutineScope(Dispatchers.Main).launch {
            alarmEntity = DataController(this@AlarmWakeSirenActivity).getAlarmData(id)!!
            amount = alarmEntity.temp!!.toInt()
            initSound()
            initButtonGroup()
            initReleaseButton()
        }
    }

    //뒤로가기 방지
    override fun onBackPressed() {}

    override fun onPause() {
        super.onPause()

        alarmEntity.snooze = true
        DataController(this).alarmDataUpdate(alarmEntity)

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        media.release()
        vibrate.cancel()
    }

    private fun initWakeLock() {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else
            window.addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
    }

    private fun initSound() {
        val uri = String.format(
            "android.resource://%s/%s/%s",
            this.packageName,
            "raw",
            R.raw.siren
        )

        media.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )

        media.setDataSource(this, Uri.parse(uri))
        media.isLooping = true
        media.prepare()
        media.start()

        if (alarmEntity.vibration) {
            val timing = longArrayOf(100, 100, 400)
            val amplitudes = intArrayOf(0, 150, 255)

            vibrate.vibrate(VibrationEffect.createWaveform(timing, amplitudes, 0))
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initButtonGroup() {
        val random = Random()

        for (layout in 0 until 3) {
            val linearLayout = binding.alarmButtonGroup.getChildAt(layout) as LinearLayout

            for (box in 0 until 3) {
                val buttonView = linearLayout.getChildAt(box) as CheckBox
                val num = buttonList.size
                buttonList.add(buttonView)

                buttonView.setOnCheckedChangeListener { view, isChecked ->
                    if (isChecked) {
                        view.background = getDrawable(R.drawable.button_selected)
                        answer.add(num)
                    } else {
                        view.background = getDrawable(R.drawable.button_unselected)
                        answer.remove(num)
                    }

                    answer.sort()

                    if(answer.size == randomNumList.size) {
                        var flag = true
                        for(i in 0 until answer.size) {
                            flag = answer[i] == randomNumList[i]
                        }
                        if(flag) finishAlarm()
                    }
                }
            }
        }

        while (randomNumList.size < amount) {
            val num = random.nextInt(buttonList.size)
            if (num !in randomNumList) {
                randomNumList.add(num)
            }
        }

        randomNumList.sort()
        setNumberPad()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setNumberPad() {
        if (releaseMode) {
            answer.clear()

            for (i in 0 until buttonList.size) {
                buttonList[i].isClickable = true
                buttonList[i].background = this.getDrawable(R.drawable.button_unselected)
            }

        } else {
            for (i in 0 until buttonList.size) {
                buttonList[i].isClickable = false
                buttonList[i].background = if (i in randomNumList) {
                    this.getDrawable(R.drawable.button_selected)
                } else {
                    this.getDrawable(R.drawable.button_unselected)
                }
            }
        }
    }

    private fun initReleaseButton() {
        binding.alarmWakeSirenReleaseButton.setOnClickListener {
            releaseMode = !releaseMode
            binding.alarmWakeSirenReleaseButton.text = if (releaseMode) {
                "다시 확인"
            } else {
                "알람 해제"
            }
            setNumberPad()
        }
    }

    private fun finishAlarm() {
        if (alarmEntity.days == 0) {
            DataController(this).alarmDataDelete(alarmEntity)
        } else {
            alarmEntity.snooze = false
            DataController(this).alarmDataUpdate(alarmEntity)
        }
        finish()
    }

}