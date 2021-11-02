package lowblow.annoying_alarm.activity.wakeup_activity

import android.content.Context
import android.hardware.*
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lowblow.annoying_alarm.data.alarm.AlarmEntity
import lowblow.annoying_alarm.databinding.AlarmWakeMosquitoBinding
import lowblow.annoying_alarm.system_manager.AlarmController
import lowblow.annoying_alarm.system_manager.DataController
import kotlin.math.abs

class AlarmWakeMosquitoActivity: AppCompatActivity(), SensorEventListener {

    private val binding by lazy {
        AlarmWakeMosquitoBinding.inflate(layoutInflater)
    }

    private val media = MediaPlayer()

    private val vibrate by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    //Sensor
    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val accSensor by lazy {
        sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
    }

    //Sensor Calc
    private var count = 10
    private var lastUpdate = System.currentTimeMillis()
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    //AlarmEntity
    private lateinit var alarmEntity: AlarmEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val id = intent.getLongExtra("id", 0)

        initSensor()
        initWakeLock()
        binding.alarmWakeCustomCountTextView.text = count.toString()

        CoroutineScope(Dispatchers.Main).launch {
            alarmEntity = DataController(this@AlarmWakeMosquitoActivity).getAlarmData(id)!!
            initSound()
        }
    }

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
        sensorManager.unregisterListener(this)
    }

    private fun initSensor() {
        sensorManager.registerListener(this,
            accSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
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
        if (alarmEntity.alarmSoundUri != null) {
            media.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            media.setDataSource(this, Uri.parse(alarmEntity.alarmSoundUri))
            media.isLooping = true
            media.prepare()
            media.start()
        }

        if (alarmEntity.vibration) {
            val timing = longArrayOf(0, 500)
            val amplitudes = intArrayOf(0, 255)

            vibrate.vibrate(VibrationEffect.createWaveform(timing, amplitudes, 0))
        }

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == accSensor) {
            val curTime = System.currentTimeMillis()

            if ((curTime - lastUpdate) > 150) {
                val diffTime = (curTime - lastUpdate)
                lastUpdate = curTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val speed = abs(x + y + z - (lastX + lastY + lastZ)) / diffTime * 10000

                if (speed > SHAKE_THRESHOLD) {
                    binding.alarmWakeCustomCountTextView.text = count.toString()

                    count -= 1

                    if(count <= 0) {
                        finishAlarm()
                    }
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun finishAlarm() {
        if (alarmEntity.days == 0) {
            DataController(this).alarmDataDelete(alarmEntity)
        } else {
            alarmEntity.snooze = false
            DataController(this).alarmDataUpdate(alarmEntity)
        }

        finish()
    }

    companion object {
        const val SHAKE_THRESHOLD = 3000
    }
}