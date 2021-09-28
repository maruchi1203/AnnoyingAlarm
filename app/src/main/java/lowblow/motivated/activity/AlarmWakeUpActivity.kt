package lowblow.motivated.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import lowblow.motivated.receiver.AlarmReceiver
import lowblow.motivated.databinding.AlarmWakeCustomBinding

class AlarmWakeUpActivity : AppCompatActivity() {

    private val binding by lazy {
        AlarmWakeCustomBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val id = intent.getIntExtra("id", 0)

        initSnoozeButton()
        initExitButton()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun initSnoozeButton() {
        binding.alarmSnoozeButton.setOnClickListener {
            val intent = Intent(this, AlarmReceiver::class.java)
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val trigger = (SystemClock.elapsedRealtime() + 60 * 1000 * 5)

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                trigger,
                pendingIntent
            )

            Toast.makeText(this, "미루기", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initExitButton() {
        finish()
    }
}