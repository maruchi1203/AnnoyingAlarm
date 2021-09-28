package lowblow.AnnoyingAlarm.fragment

import androidx.fragment.app.Fragment
import lowblow.AnnoyingAlarm.data.Mode
import lowblow.AnnoyingAlarm.data.alarm.AlarmFragmentData

abstract class FragmentParent : Fragment() {

    open fun getData() : AlarmFragmentData {
        return AlarmFragmentData(
            null,
            false,
            0.toFloat(),
            false,
            Mode.FRAGMENT_CUSTOM
        )
    }

}