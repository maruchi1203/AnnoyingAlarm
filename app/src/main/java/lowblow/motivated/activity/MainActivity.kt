package lowblow.motivated.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lowblow.motivated.adapter.AlarmListAdapter
import lowblow.motivated.data.alarm.AlarmDatabase
import lowblow.motivated.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var database: AlarmDatabase

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

        database = AlarmDatabase.getInstance(this)!!
        binding.alarmRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        initAlarmButton()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.alarmRecyclerView.adapter = AlarmListAdapter(database.alarmDao().getAllAlarms(), this@MainActivity)
                (binding.alarmRecyclerView.adapter as AlarmListAdapter).apply {
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun initAlarmButton() {
        binding.addAlarmButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, AlarmSettingActivity::class.java))
        }
    }
}