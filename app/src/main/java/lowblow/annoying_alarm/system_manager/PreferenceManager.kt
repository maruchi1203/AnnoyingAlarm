package lowblow.annoying_alarm.system_manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(val context: Context) {

    private val pref: SharedPreferences =
        context.applicationContext.getSharedPreferences("Motivated", Context.MODE_PRIVATE)

    private val defBool = true

    fun getBoolean(key : String) : Boolean {
        return pref.getBoolean(key, defBool)
    }

    @SuppressLint("CommitPrefEdits")
    fun setBoolean(key : String, bool : Boolean) {
        pref.edit().putBoolean(key, bool).apply()
    }
}