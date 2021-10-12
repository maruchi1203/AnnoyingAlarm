package lowblow.annoying_alarm.fragment

import android.content.DialogInterface
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import lowblow.annoying_alarm.activity.AlarmSoundActivity
import lowblow.annoying_alarm.R
import lowblow.annoying_alarm.data.Mode
import lowblow.annoying_alarm.data.alarm.AlarmFragmentData
import lowblow.annoying_alarm.databinding.FragmentCustomBinding

class FragmentCustom : FragmentParent() {

    private lateinit var binding: FragmentCustomBinding

    //Media player
    private val media = MediaPlayer()

    //For initMusicChangeButton
    private lateinit var resultListener: ActivityResultLauncher<Intent>
    private lateinit var alertBuilder: AlertDialog.Builder

    //For initAlarmPlayTestButton
    private var isStopped: Boolean = true

    //For AlarmFragmentData
    private var selectedUri: Uri? = null
    private var loudness: Float = 1.toFloat()
    private var isVibrate = true
    private var isGentleAlarm = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomBinding.inflate(layoutInflater)

        initMusicChangeButton()
        initAlarmPlayTestButton()
        initVibrationSwitch()
        initSeekbar()
        initGentleAlarm()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        media.release()
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
                    selectedUri = Uri.parse(result.data!!.getStringExtra("selectedUri"))
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
                    0-> {
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
    }

    private fun controlSoundControlLayouts(isExisting: Boolean) {
        binding.clickBlockingView.isClickable = !isExisting
        if (isExisting) {
            binding.alarmSoundControlTextView.alpha = 1f
            binding.alarmSoundControlSeekBar.alpha = 1f
            binding.alarmGentleSoundLinearLayout.alpha = 1f
            binding.alarmPlayTestButton.isGone = false
        } else {
            binding.alarmSoundControlTextView.alpha = 0.1f
            binding.alarmSoundControlSeekBar.alpha = 0.1f
            binding.alarmGentleSoundLinearLayout.alpha = 0.1f
            binding.alarmPlayTestButton.isGone = true
        }
    }

    private fun initAlarmPlayTestButton() {

        binding.alarmPlayTestButton.setOnClickListener {
            if (isStopped) {
                if (selectedUri != null) {
                    media.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    media.setDataSource(requireContext(), selectedUri!!)
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

    private fun initGentleAlarm() {
        binding.alarmGentleSoundSwitch.setOnCheckedChangeListener { _, isChecked ->
            isGentleAlarm = isChecked
        }
    }

    override fun getData(): AlarmFragmentData {

        return AlarmFragmentData(
            selectedUri?.toString(),
            isVibrate,
            loudness,
            isGentleAlarm,
            Mode.FRAGMENT_CUSTOM
        )

    }

    override fun setData(data: AlarmFragmentData) {

    }

    companion object {
        const val RESULT_CODE = 101
    }
}