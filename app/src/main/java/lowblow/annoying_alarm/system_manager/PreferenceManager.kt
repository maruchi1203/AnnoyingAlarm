package lowblow.annoying_alarm.system_manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE

class PreferenceManager(val context: Context) {

    private val pref = context.getSharedPreferences("AnnoyingAlarm", MODE_PRIVATE)
    private var defBool = true

    fun getBoolean(key : String) : Boolean {
        return pref.getBoolean(key, defBool)
    }

    @SuppressLint("CommitPrefEdits")
    fun setBoolean(key : String, bool : Boolean) {
        pref.edit().putBoolean(key, bool).apply()
    }
}