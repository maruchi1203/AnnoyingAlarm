package lowblow.AnnoyingAlarm.system_manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import lowblow.AnnoyingAlarm.data.alarm.AlarmEntity
import lowblow.AnnoyingAlarm.receiver.AlarmReceiver
import java.util.*

class AlarmController(private val context: Context) {

    private val alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
    private val intent = Intent(context, AlarmReceiver::class.java)

    fun setAlarm(alarmEntity : AlarmEntity) {

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarmEntity.hour)
            set(Calendar.MINUTE, alarmEntity.minute)
            set(Calendar.SECOND, 0)
        }

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            alarmEntity.id,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun snoozeAlarm() {
        val intent = Intent(context, AlarmReceiver::class.java)

        val trigger = (SystemClock.elapsedRealtime() + 60 * 1000 * 5)

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            trigger,
            pendingIntent
        )

        Toast.makeText(context, "미루기", Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(alarmEntity : AlarmEntity) {
        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            alarmEntity.id,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }
}