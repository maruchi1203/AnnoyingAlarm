package lowblow.annoying_alarm.fragment

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import lowblow.annoying_alarm.R
import lowblow.annoying_alarm.data.AlarmType
import lowblow.annoying_alarm.data.alarm.AlarmFragmentData
import lowblow.annoying_alarm.databinding.FragmentMosquitoBinding

class FragmentMosquito : FragmentParent() {

    private val binding by lazy {
        FragmentMosquitoBinding.inflate(layoutInflater)
    }

    //AlertDialog for SoundChangeButton
    private lateinit var alertBuilder: AlertDialog.Builder

    //Media player
    private lateinit var media: MediaPlayer

    //For initAlarmPlayTestButton
    private var isStopped: Boolean = true

    //For AlarmFragmentData
    //AlarmFragmentData.temp1 == selectedTitle for custom fragment
    //AlarmFragmentData.temp2 == selectedUri for custom fragment
    private var selectedTitle: String? = null
    private var selectedUri: String? = null
    private var volume: Float? = null
    private var isVibrate = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        media = MediaPlayer()
        initViews()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        media.release()
    }

    private fun initViews() {
        initAlarmFragmentDataValue()
        initAlarmSoundChangeButton()
        initAlarmPlayTestButton()
        initVibrationSwitch()
        initSeekbar()
    }

    private fun initAlarmFragmentDataValue() {
        requireArguments().getSerializable("AlarmFragmentData")?.let {
            val alarmFragmentData = it as AlarmFragmentData
            if (alarmFragmentData.alarmType == AlarmType.FRAGMENT_MOSQUITO) {
                selectedTitle = alarmFragmentData.alarmUri
                selectedUri = alarmFragmentData.temp
            }
            isVibrate = alarmFragmentData.vibration
            volume = alarmFragmentData.volume
        }
    }

    private fun initAlarmSoundChangeButton() {
        if (selectedUri == null) {
            selectedUri = String.format(
                "android.resource://%s/%s/%s",
                requireContext().packageName,
                "raw",
                R.raw.mosquito1
            )
            selectedTitle = "모기소리1"
        }

        alertBuilder = AlertDialog.Builder(requireContext())

        binding.alarmSoundChangeButton.text = selectedTitle

        binding.alarmSoundChangeButton.setOnClickListener {
            alertBuilder.setItems(R.array.mosquito_sound_type) { _, which ->
                when (which) {
                    0 -> {
                        selectedUri =
                            String.format(
                                "android.resource://%s/%s/%s",
                                requireContext().packageName,
                                "raw",
                                R.raw.mosquito1
                            )
                        selectedTitle = "모기소리1"
                    }
                    1 -> {
                        selectedUri =
                            String.format(
                                "android.resource://%s/%s/%s",
                                requireContext().packageName,
                                "raw",
                                R.raw.mosquito2
                            )
                        selectedTitle = "모기소리2"
                    }
                }

                binding.alarmSoundChangeButton.text = selectedTitle
            }.create().show()
        }
    }

    private fun initAlarmPlayTestButton() {

        binding.alarmPlayTestButton.setOnClickListener {
            if (isStopped) {
                media.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                media.setDataSource(requireContext(), Uri.parse(selectedUri))
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

    private fun initSeekbar() {
        if (volume == null) {
            binding.alarmSoundControlSeekBar.progress = 100
            volume = binding.alarmSoundControlSeekBar.progress.toFloat() / 100
        } else {
            binding.alarmSoundControlSeekBar.progress = (volume!! * 100).toInt()
        }

        media.setVolume(volume!!, volume!!)

        binding.alarmSoundControlSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                volume = progress.toFloat() / 100
                media.setVolume(volume!!, volume!!)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}


        })
    }

    override fun getData(): AlarmFragmentData {

        return AlarmFragmentData(
            isVibrate,
            volume!!,
            AlarmType.FRAGMENT_MOSQUITO,
            selectedTitle,
            selectedUri
        )

    }
}