package lowblow.annoying_alarm.activity

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.INTERNET
            ),
            MODE_PRIVATE
        )

        binding.alarmRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.alarmRecyclerView.adapter = adapter

        initAlarmButton()
    }

    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.Main).launch {
            adapter.submitList(DataController(this@MainActivity).getAllAlarmData())
        }
    }

    private fun initAlarmButton() {
        binding.addAlarmButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, AlarmSettingActivity::class.java))
        }
    }
}