package lowblow.annoying_alarm.fragment

import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lowblow.annoying_alarm.R
import lowblow.annoying_alarm.data.AlarmType
import lowblow.annoying_alarm.data.alarm.AlarmFragmentData
import lowblow.annoying_alarm.databinding.FragmentMessengerBinding

class FragmentMessenger : FragmentParent() {

    private val binding by lazy {
        FragmentMessengerBinding.inflate(layoutInflater)
    }

    //For initAlarmPlayTestButton
    private lateinit var rt: Ringtone

    //For AlarmFragmentData
    //AlarmFragmentData.temp ==  for Messenger fragment
    private var isVibrate = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        initAlarmFragmentDataValue()
        initAlarmPlayTestButton()
        initVibrationSwitch()

        return binding.root
    }

    private fun initAlarmFragmentDataValue() {
        requireArguments().getSerializable("AlarmFragmentData")?.let {
            val alarmFragmentData = it as AlarmFragmentData
            isVibrate = alarmFragmentData.vibration
        }
    }

    private fun initAlarmPlayTestButton() {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        rt = RingtoneManager.getRingtone(requireContext().applicationContext, soundUri)

        binding.alarmPlayTestButton.setOnClickListener {
            rt.audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            rt.play()
        }
    }

    private fun initVibrationSwitch() {
        val vibrationSwitch = binding.alarmVibrationSwitchCompat

        vibrationSwitch.isChecked = !isVibrate

        vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            isVibrate = !isChecked
            if (isVibrate) {
                vibrationSwitch.setThumbResource(R.drawable.ic_baseline_vibration_24)
            } else {
                vibrationSwitch.setThumbResource(R.drawable.ic_baseline_vibration_cancel_24)
            }
        }
    }

    override fun getData(): AlarmFragmentData {

        return AlarmFragmentData(
            isVibrate,
            1F,
            AlarmType.FRAGMENT_MESSENGER,
            null,
            null,
        )

    }
}