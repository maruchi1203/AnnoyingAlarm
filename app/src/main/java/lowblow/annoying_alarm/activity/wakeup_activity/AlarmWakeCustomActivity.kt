package lowblow.annoying_alarm.activity.wakeup_activity

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lowblow.annoying_alarm.data.alarm.AlarmEntity
import lowblow.annoying_alarm.databinding.AlarmWakeCustomBinding
import lowblow.annoying_alarm.system_manager.AlarmController
import lowblow.annoying_alarm.system_manager.DataController
import java.lang.Exception
import kotlin.concurrent.thread

class AlarmWakeCustomActivity : AppCompatActivity() {

    private val binding by lazy {
        AlarmWakeCustomBinding.inflate(layoutInflater)
    }

    private val media = MediaPlayer()
    private val vibrate by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    private lateinit var alarmEntity: AlarmEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val id = intent.getLongExtra("id", 0)

        initWakeLock()

        CoroutineScope(Dispatchers.Main).launch {
            alarmEntity = DataController(this@AlarmWakeCustomActivity).getAlarmData(id)!!
            initSound()
            initSnoozeButton()
            initExitButton()
        }
    }

    //뒤로가기 방지
    override fun onBackPressed() {}

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
        if (alarmEntity.alarmUri != null) {
            media.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            media.setDataSource(this, Uri.parse(alarmEntity.alarmUri))
            media.isLooping = true
            media.prepare()
            media.start()

            if (alarmEntity.gentleAlarm) {
                try {
                    thread(true) {
                        for (i in 0..(alarmEntity.loudness * 100).toInt()) {
                            media.setVolume(i.toFloat() / 100, i.toFloat() / 100)

                            Thread.sleep(1000)
                        }
                    }
                }
                catch (e : Exception) {}
            } else {
                media.setVolume(alarmEntity.loudness, alarmEntity.loudness)
            }
        }

        if (alarmEntity.vibration) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                vibrate.vibrate(1000)
            } else {
                val timing = longArrayOf(100, 100, 400)
                val amplitudes = intArrayOf(0, 150, 255)

                vibrate.vibrate(VibrationEffect.createWaveform(timing, amplitudes, 0))
            }
        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun initSnoozeButton() {
        binding.alarmSnoozeButton.setOnClickListener {
            AlarmController(this).snoozeAlarm()
            media.release()
            vibrate.cancel()

            finish()
        }
    }

    private fun initExitButton() {
        binding.alarmWakeCustomCloseButton.setOnClickListener {
            media.release()
            vibrate.cancel()

            if (alarmEntity.days == 0) {
                DataController(this).alarmDataDelete(alarmEntity)
            } else {
                AlarmController(this).setAlarm(alarmEntity.id, alarmEntity)
            }

            finish()
        }
    }
}