package lowblow.annoying_alarm.activity.wakeup_activity

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
import lowblow.annoying_alarm.system_manager.DataController

class AlarmWakeCustomActivity : AppCompatActivity() {

    private val binding by lazy {
        AlarmWakeCustomBinding.inflate(layoutInflater)
    }

    private lateinit var media: MediaPlayer
    private val vibrate by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    private lateinit var alarmEntity: AlarmEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        media = MediaPlayer()

        val id = intent.getLongExtra("id", 0)

        initWakeLock()

        CoroutineScope(Dispatchers.Main).launch {
            alarmEntity = DataController(this@AlarmWakeCustomActivity).getAlarmData(id)!!
            initSound()
            initExitButton()
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
        if (alarmEntity.temp != null) {
            media.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            media.setDataSource(this, Uri.parse(alarmEntity.temp))
            media.isLooping = true
            media.prepare()
            media.start()
        }

        if (alarmEntity.vibration) {
            val timing = longArrayOf(100, 100, 400)
            val amplitudes = intArrayOf(0, 150, 255)

            vibrate.vibrate(VibrationEffect.createWaveform(timing, amplitudes, 0))
        }

    }

    private fun initExitButton() {
        binding.alarmWakeCustomCloseButton.setOnClickListener {
            if (alarmEntity.days == 0) {
                DataController(this).alarmDataDelete(alarmEntity)
            } else {
                alarmEntity.snooze = false
                DataController(this).alarmDataUpdate(alarmEntity)
            }

            finish()
        }
    }
}