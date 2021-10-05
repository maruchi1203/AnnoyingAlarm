package lowblow.annoying_alarm.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lowblow.annoying_alarm.adapter.AlarmListAdapter
import lowblow.annoying_alarm.databinding.ActivityMainBinding
import lowblow.annoying_alarm.system_manager.DataController
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val adapter by lazy {
        AlarmListAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSetting()

        binding.alarmRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.alarmRecyclerView.adapter = adapter

        initAlarmButton()
    }

    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.IO).launch {
            adapter.submitList(DataController(this@MainActivity).getAllAlarmData())
        }
    }

    private fun initSetting() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            MODE_PRIVATE
        )

        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "다른 앱 위에 그리기를 허용해주세요", Toast.LENGTH_SHORT).show()
            val uri = Uri.fromParts("package", packageName, null)
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
            startActivity(intent)
        }

        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "다른 앱 위에 그리기가 허용되지 않았습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initAlarmButton() {
        binding.addAlarmButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, AlarmSettingActivity::class.java))
        }
    }
}