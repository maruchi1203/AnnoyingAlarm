package lowblow.annoying_alarm.activity.wakeup_activity

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lowblow.annoying_alarm.adapter.MessageListAdapter
import lowblow.annoying_alarm.data.MessageEntity
import lowblow.annoying_alarm.data.alarm.AlarmEntity
import lowblow.annoying_alarm.databinding.AlarmWakeMessengerBinding
import lowblow.annoying_alarm.system_manager.DataController
import java.util.*

class AlarmWakeMessengerActivity : AppCompatActivity() {

    private val binding by lazy {
        AlarmWakeMessengerBinding.inflate(layoutInflater)
    }
    private val vibrate by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    private lateinit var keyguardManager: KeyguardManager
    private lateinit var alarmEntity: AlarmEntity

    private val adapter by lazy {
        MessageListAdapter(this)
    }
    private val timer by lazy {
        Timer()
    }
    private lateinit var rt: Ringtone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.messageRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.messageRecyclerView.adapter = adapter

        val id = intent.getLongExtra("id", 0)

        CoroutineScope(Dispatchers.Main).launch {
            alarmEntity = DataController(this@AlarmWakeMessengerActivity).getAlarmData(id)!!
            binding.alarmMemoTextView.text = alarmEntity.memo
            initWakeLock()
            initSound()
            initMessageOutput()
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

        rt.stop()
        timer.cancel()
    }

    private fun initWakeLock() {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else
            window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
    }

    private fun initSound() {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        rt = RingtoneManager.getRingtone(this.applicationContext, soundUri)
        rt.audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
            .build()
    }

    private fun initMessageOutput() {
        var idx = 0
        val outputMessage =
            listOf(
                "알람 켜졌어요",
                "저기요?",
                "아직 못 봤나",
                "ㅇ",
                "오, 봤네요?",
                "알람을 끄려면 '알람 종료'라고 써주세요",
                "대답 없으면 다시 문자 보낼 거에요"
            )

        var innerTimeFlag = true
        val timerTask = object : TimerTask() {
            override fun run() {
                this@AlarmWakeMessengerActivity.runOnUiThread {
                    if (idx == 7) {
                        if(innerTimeFlag) {
                            val innerTimerTask = object : TimerTask() {
                                override fun run() {
                                    this@AlarmWakeMessengerActivity.runOnUiThread {
                                        idx = 3
                                        innerTimeFlag = true
                                    }
                                }
                            }
                            innerTimeFlag = false
                            timer.schedule(innerTimerTask, 10000)
                        }
                    } else {
                        updateMessageList(
                            adapter.itemCount,
                            false,
                            outputMessage[idx]
                        )
                        if (idx != 3) idx += 1
                    }

                    binding.messageRecyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }

        binding.alarmInputConfirmButton.setOnClickListener {
            if (binding.alarmInputEditText.text.isNotBlank()) {
                val answer = binding.alarmInputEditText.text.toString()

                if (idx == 3) {
                    idx += 1
                }

                if (answer == "알람 종료") {
                    finishAlarm()
                }

                updateMessageList(
                    adapter.itemCount,
                    true,
                    answer
                )

                binding.alarmInputEditText.setText("")

                binding.messageRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        }

        timer.schedule(timerTask, 0, 1500)
    }

    private fun updateMessageList(itemCount: Int, isUser: Boolean, message: String) {
        adapter.updateMessageList(
            MessageEntity(
                itemCount,
                isUser,
                message
            )
        )

        if (!isUser) {
            if (alarmEntity.vibration) {
                vibrate.vibrate(VibrationEffect.createOneShot(200, 255))
            }
            rt.play()
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