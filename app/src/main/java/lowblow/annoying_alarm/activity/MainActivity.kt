package lowblow.annoying_alarm.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lowblow.annoying_alarm.adapter.AlarmListAdapter
import lowblow.annoying_alarm.databinding.ActivityMainBinding
import lowblow.annoying_alarm.system_manager.DataController
import lowblow.annoying_alarm.system_manager.PreferenceManager
import java.security.Permission
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val adapter by lazy {
        AlarmListAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSetting()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAlarmButton()
        initPreferenceMenu()

        binding.alarmRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.alarmRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        adapter.refresh()
    }

    override fun onRestart() {
        super.onRestart()

        if (checkPermission()) {
            finish()
        }
    }

    private fun initSetting() {
        if (checkPermission()) {
            startActivity(Intent(this, PermissionActivity::class.java))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initPreferenceMenu() {
        val preferenceManager = PreferenceManager(this)
        binding.option24HourSwitch.isChecked = preferenceManager.getBoolean("24Hour")
        binding.optionRepeatWeekdaySwitch.isChecked = preferenceManager.getBoolean("repeatAlarmForWeekDay")

        binding.option24HourSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                preferenceManager.setBoolean("24Hour", true)
            } else {
                preferenceManager.setBoolean("24Hour", false)
            }
            adapter.notifyDataSetChanged()
        }
        binding.optionRepeatWeekdaySwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                preferenceManager.setBoolean("repeatAlarmForWeekDay", true)
            } else {
                preferenceManager.setBoolean("repeatAlarmForWeekDay", false)
            }
        }
    }

    private fun checkPermission(): Boolean {
        return !Settings.canDrawOverlays(this) or (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED)
    }

    private fun initAlarmButton() {
        binding.addAlarmButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, AlarmSettingActivity::class.java))
        }
    }
}