package lowblow.annoying_alarm.activity

import android.Manifest
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

        binding.alarmRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.alarmRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.Main).launch {
            adapter.submitList(DataController(this@MainActivity).getAllAlarmData())
        }
    }

    override fun onRestart() {
        super.onRestart()

        if(checkPermission()) {
            finish()
        }
    }

    private fun initSetting() {
        if (checkPermission()) {
            startActivity(Intent(this, PermissionActivity::class.java))
        }
    }

    private fun initPreferenceMenu() {
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