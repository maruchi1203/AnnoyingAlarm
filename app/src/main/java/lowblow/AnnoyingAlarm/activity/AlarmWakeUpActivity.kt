package lowblow.AnnoyingAlarm.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import lowblow.AnnoyingAlarm.databinding.AlarmWakeCustomBinding
import lowblow.AnnoyingAlarm.system_manager.AlarmController

class AlarmWakeUpActivity : AppCompatActivity() {

    private val binding by lazy {
        AlarmWakeCustomBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val id = intent.getIntExtra("id", 0)

        initMusic()
        initSnoozeButton()
        initExitButton()
    }

    private fun initMusic() {

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun initSnoozeButton() {
        binding.alarmSnoozeButton.setOnClickListener {
            AlarmController(this).snoozeAlarm()
        }
    }

    private fun initExitButton() {
        binding.alarmWakeCustomCloseButton.setOnClickListener {

            finish()
        }
    }
}