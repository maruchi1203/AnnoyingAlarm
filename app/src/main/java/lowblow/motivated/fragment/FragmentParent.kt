package lowblow.motivated.fragment

import androidx.fragment.app.Fragment
import lowblow.motivated.data.Mode
import lowblow.motivated.data.alarm.AlarmFragmentData

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