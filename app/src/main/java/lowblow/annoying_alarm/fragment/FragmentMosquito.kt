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
    private val media = MediaPlayer()

    //For initAlarmPlayTestButton
    private var isStopped: Boolean = true

    //For AlarmFragmentData
    private lateinit var selectedUri : Uri
    private var loudness: Float = 1.toFloat()
    private var isVibrate = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViews()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        media.release()
    }

    private fun initViews() {
        initAlarmSoundChangeButton()
        initAlarmPlayTestButton()
        initVibrationSwitch()
        initSeekbar()
    }

    private fun initAlarmSoundChangeButton() {
        selectedUri = Uri.parse(
            String.format(
                "android.resource://%s/%s/%s",
                requireContext().packageName,
                "raw",
                R.raw.mosquito1
            )
        )

        alertBuilder = AlertDialog.Builder(requireContext())

        binding.alarmSoundChangeButton.setOnClickListener {
            alertBuilder.setItems(R.array.mosquito_sound_type) { _, which ->
                when (which) {
                    0 -> {
                        selectedUri =
                            Uri.parse(
                                String.format(
                                    "android.resource://%s/%s/%s",
                                    requireContext().packageName,
                                    "raw",
                                    R.raw.mosquito1
                                )
                            )

                        binding.alarmSoundChangeButton.text = "모기소리1"
                    }
                    1 -> {
                        selectedUri =
                            Uri.parse(
                                String.format(
                                    "android.resource://%s/%s/%s",
                                    requireContext().packageName,
                                    "raw",
                                    R.raw.mosquito2
                                )
                            )
                        binding.alarmSoundChangeButton.text = "모기소리2"
                    }
                }
            }.create().show()
        }
    }

    private fun initAlarmPlayTestButton() {

        binding.alarmPlayTestButton.setOnClickListener {
            if (isStopped) {
                media.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                media.setDataSource(requireContext(), selectedUri)
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
        binding.alarmSoundControlSeekBar.progress = 100
        loudness = binding.alarmSoundControlSeekBar.progress.toFloat() / 100
        media.setVolume(loudness, loudness)

        binding.alarmSoundControlSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                loudness = progress.toFloat() / 100
                media.setVolume(loudness, loudness)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}


        })
    }

    override fun getData(): AlarmFragmentData {

        return AlarmFragmentData(
            selectedUri.toString(),
            isVibrate,
            loudness,
            false,
            AlarmType.FRAGMENT_MOSQUITO
        )

    }
}