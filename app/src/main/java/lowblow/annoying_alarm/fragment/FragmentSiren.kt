package lowblow.annoying_alarm.fragment

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import lowblow.annoying_alarm.R
import lowblow.annoying_alarm.data.AlarmType
import lowblow.annoying_alarm.data.alarm.AlarmFragmentData
import lowblow.annoying_alarm.databinding.FragmentSirenBinding

class FragmentSiren : FragmentParent() {

    private val binding by lazy {
        FragmentSirenBinding.inflate(layoutInflater)
    }

    //Media player
    private lateinit var media: MediaPlayer

    //For initMusicChangeButton
    private lateinit var alertBuilder: AlertDialog.Builder

    //For initAlarmPlayTestButton
    private var isStopped: Boolean = true

    //For AlarmFragmentData
    private var alarmUri: String? = null;
    private var amount: Int = 1
    private var isVibrate = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        media = MediaPlayer()

        initAlarmFragmentDataValue()
        initPadSizeChangeButton()
        initAlarmPlayTestButton()
        initVibrationSwitch()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        media.release()
    }

    private fun initAlarmFragmentDataValue() {
        requireArguments().getSerializable("AlarmFragmentData")?.let {
            val alarmFragmentData = it as AlarmFragmentData
            if (alarmFragmentData.alarmType == AlarmType.FRAGMENT_SIREN) {
                alarmUri = alarmFragmentData.alarmUri!!
                amount = alarmFragmentData.temp!!.toInt()
            }
            isVibrate = alarmFragmentData.vibration
        }
    }

    private fun initAlarmPlayTestButton() {
        if (alarmUri == null) {
            alarmUri = String.format(
                "android.resource://%s/%s/%s",
                requireContext().packageName,
                "raw",
                R.raw.siren
            )
        }

        binding.alarmPlayTestButton.setOnClickListener {
            if (isStopped) {
                media.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                media.setDataSource(requireContext(), Uri.parse(alarmUri))
                media.isLooping = true
                media.prepare()
                media.start()

                isStopped = false
                binding.alarmPlayTestButton.setImageResource(R.drawable.ic_baseline_stop_24)
            } else {
                media.reset()
                isStopped = true
                binding.alarmPlayTestButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
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

    private fun initPadSizeChangeButton() {
        val spinnerItems = mutableListOf<Int>()
        for (i in 1..9) {
            spinnerItems.add(i)
        }
        val spinnerAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                spinnerItems
            )
        binding.alarmAmountSpinner.adapter = spinnerAdapter

        binding.alarmAmountSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    amount = position + 1
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    override fun getData(): AlarmFragmentData {

        return AlarmFragmentData(
            isVibrate,
            1F,
            AlarmType.FRAGMENT_SIREN,
            alarmUri,
            amount.toString()
        )

    }
}