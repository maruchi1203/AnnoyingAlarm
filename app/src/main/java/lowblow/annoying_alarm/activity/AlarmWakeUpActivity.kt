package lowblow.annoying_alarm.activity

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lowblow.annoying_alarm.data.alarm.AlarmEntity
import lowblow.annoying_alarm.databinding.AlarmWakeCustomBinding
import lowblow.annoying_alarm.system_manager.AlarmController
import lowblow.annoying_alarm.system_manager.DataController
import kotlin.concurrent.thread

class AlarmWakeUpActivity : AppCompatActivity() {

    private val binding by lazy {
        AlarmWakeCustomBinding.inflate(layoutInflater)
    }

    private val media = MediaPlayer()
    private val vibrate by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    private lateinit var data: AlarmEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val id = intent.getLongExtra("id", 0)

        Log.e("AlarmWakeUpActivity", id.toString())

        CoroutineScope(Dispatchers.Main).launch {
            data = DataController(this@AlarmWakeUpActivity).getAlarmData(id)!!
            initSound()
            initSnoozeButton()
            initExitButton()
        }
    }

    private fun initSound() {
        if(data.alarmUri != null) {
            media.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            media.setDataSource(this, Uri.parse(data.alarmUri))
            media.prepare()
            media.start()

            if(data.gentleAlarm) {
                thread(start = true) {
                    for (i in 0..(data.loudness*100).toInt()) {
                        media.setVolume(i.toFloat() / 100, i.toFloat() / 100)

                        Thread.sleep(1000)
                    }
                }

            } else {
                media.setVolume(data.loudness, data.loudness)
            }
        }

        if(data.vibration) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
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
            media.reset()
            vibrate.cancel()
            finish()
        }
    }

    private fun initExitButton() {
        binding.alarmWakeCustomCloseButton.setOnClickListener {
            media.reset()
            vibrate.cancel()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(data.days == 0) {
            DataController(this).alarmDataDelete(data)
        } else {
            AlarmController(this).setAlarm(data.id, data)
        }
    }
}