package lowblow.annoying_alarm.system_manager

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log

class PreferenceManager(val context: Context) {

    private val pref by lazy {
        context.applicationContext.getSharedPreferences("AnnoyingAlarm", MODE_PRIVATE)
    }
    private var defBool = false

    fun getBoolean(key: String): Boolean {
        return pref.getBoolean(key, defBool)
    }

    fun setBoolean(key: String, bool: Boolean) {
        pref.edit().putBoolean(key, bool).apply()
    }

    /*
    * Preference
    *
    * 24Hour == 24시간 표기
    * repeatAlarmForWeekDay == 알람 생성시 평일 반복으로 자동설정할지
    *
    * */
}