package lowblow.annoying_alarm.fragment

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import lowblow.annoying_alarm.activity.AlarmSoundActivity
import lowblow.annoying_alarm.R
import lowblow.annoying_alarm.data.AlarmType
import lowblow.annoying_alarm.data.alarm.AlarmFragmentData
import lowblow.annoying_alarm.databinding.FragmentCustomBinding

class FragmentCustom : FragmentParent() {

    private val binding by lazy {
        FragmentCustomBinding.inflate(layoutInflater)
    }

    //Media player
    private lateinit var media: MediaPlayer

    //For initMusicChangeButton
    private lateinit var resultListener: ActivityResultLauncher<Intent>
    private lateinit var alertBuilder: AlertDialog.Builder

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
        savedInstanceState: Bundle?,
    ): View {
        media = MediaPlayer()

        initAlarmFragmentDataValue()
        initMusicChangeButton()
        initAlarmPlayTestButton()
        initVibrationSwitch()
        initVolumeChangeSeekBar()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        media.release()
    }

    private fun initAlarmFragmentDataValue() {
        requireArguments().getSerializable("AlarmFragmentData")?.let {
            val alarmFragmentData = it as AlarmFragmentData
            if(alarmFragmentData.alarmType == AlarmType.FRAGMENT_CUSTOM) {

                selectedTitle = alarmFragmentData.alarmUri
                selectedUri = alarmFragmentData.temp
            }
            isVibrate = alarmFragmentData.vibration
            volume = alarmFragmentData.volume
        }
    }

    private fun initMusicChangeButton() {
        alertBuilder = AlertDialog.Builder(requireContext())

        controlSoundControlLayouts(false)

        resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_CODE
                    && result.data != null
                    && result.data!!.getStringExtra("selectedUri").toString() != "null"
                ) {
                    selectedUri = result.data!!.getStringExtra("selectedUri")
                    selectedTitle = result.data!!.getStringExtra("selectedTitle")
                    binding.alarmMusicChangeButton.text =
                        result.data!!.getStringExtra("selectedTitle")
                    controlSoundControlLayouts(true)
                } else {
                    binding.alarmMusicChangeButton.text = "무음"
                    controlSoundControlLayouts(false)
                }
            }

        val intent = Intent(requireContext(), AlarmSoundActivity::class.java)
        binding.alarmMusicChangeButton.setOnClickListener {
            alertBuilder.setItems(
                R.array.alarm_sound_type
            ) { _, which ->
                when (which) {
                    0 -> {
                        selectedUri = null
                        binding.alarmMusicChangeButton.text = "무음"
                        controlSoundControlLayouts(false)
                    }
                    1 -> {
                        intent.putExtra("soundType", MediaStore.Audio.Media.IS_NOTIFICATION)
                        resultListener.launch(intent)
                    }
                    2 -> {
                        intent.putExtra("soundType", MediaStore.Audio.Media.IS_MUSIC)
                        resultListener.launch(intent)
                    }
                }
            }.create().show()
        }

        if(selectedUri != null) {
            binding.alarmMusicChangeButton.text = selectedTitle
            controlSoundControlLayouts(true)
        }
    }

    private fun controlSoundControlLayouts(isExisting: Boolean) {
        binding.clickBlockingView.isClickable = !isExisting
        if (isExisting) {
            binding.alarmSoundControlTextView.alpha = 1f
            binding.alarmSoundControlSeekBar.alpha = 1f
            binding.alarmPlayTestButton.isGone = false
        } else {
            binding.alarmSoundControlTextView.alpha = 0.1f
            binding.alarmSoundControlSeekBar.alpha = 0.1f
            binding.alarmPlayTestButton.isGone = true
        }
    }

    private fun initAlarmPlayTestButton() {

        binding.alarmPlayTestButton.setOnClickListener {
            if (isStopped) {
                if (selectedUri != null) {
                    media.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    media.setDataSource(requireContext(), Uri.parse(selectedUri!!))
                    media.isLooping = true
                    media.prepare()
                    media.start()

                    isStopped = false
                    binding.alarmPlayTestButton.setImageResource(R.drawable.ic_baseline_stop_24)
                }
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

    private fun initVolumeChangeSeekBar() {
        if(volume == null) {
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
            AlarmType.FRAGMENT_CUSTOM,
            selectedTitle,
            selectedUri,
        )
    }

    companion object {
        const val RESULT_CODE = 101
    }
}