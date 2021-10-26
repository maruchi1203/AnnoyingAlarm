package lowblow.annoying_alarm.fragment

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import lowblow.annoying_alarm.R
import lowblow.annoying_alarm.databinding.FragmentMosquitoBinding

class FragmentMosquito: FragmentParent() {

    private val binding by lazy {
        FragmentMosquitoBinding.inflate(layoutInflater)
    }

    //Media player
    private val media = MediaPlayer()

    //For initAlarmPlayTestButton
    private var isStopped: Boolean = true

    //For AlarmFragmentData
    private var selectedUri: Uri? = null
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

    private fun initViews() {
        initSpinner()
    }

    private fun initSpinner() {
        val spinner = binding.alarmExitTypeSpinner
        val spinnerArray = resources.getStringArray(R.array.alarm_exit_type)
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, spinnerArray)

        spinner.adapter = spinnerAdapter



    }
}