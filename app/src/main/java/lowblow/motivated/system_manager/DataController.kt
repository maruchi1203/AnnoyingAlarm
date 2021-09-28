package lowblow.motivated.system_manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lowblow.motivated.data.alarm.AlarmDatabase
import lowblow.motivated.data.alarm.AlarmEntity
import lowblow.motivated.receiver.AlarmReceiver
import java.util.*

class DataController(val context: Context) {

    private val database = AlarmDatabase.getInstance(context.applicationContext)

    fun getAlarm(id : Int): AlarmEntity? {
        var alarmEntity: AlarmEntity? = null
        CoroutineScope(Dispatchers.IO).launch {
            alarmEntity = database!!.alarmDao().getAlarm(id)
        }
        return alarmEntity
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun alarmCreate(alarmEntity: AlarmEntity) {
        val alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarmEntity.hour)
            set(Calendar.MINUTE, alarmEntity.minute)
            set(Calendar.SECOND, 0)
        }

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }

        CoroutineScope(Dispatchers.IO).launch {
            database!!.alarmDao().insertAlarm(
                alarmEntity
            )
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun alarmUpdate(alarmEntity: AlarmEntity) {

        CoroutineScope(Dispatchers.IO).launch {
            database!!.alarmDao().updateAlarm(alarmEntity)
        }

    }

    fun alarmDelete(alarmEntity: AlarmEntity) {

        CoroutineScope(Dispatchers.IO).launch {
            database!!.alarmDao().deleteAlarm(alarmEntity)
        }

    }

}