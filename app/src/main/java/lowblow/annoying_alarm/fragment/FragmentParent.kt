package lowblow.annoying_alarm.fragment

import androidx.fragment.app.Fragment
import lowblow.annoying_alarm.data.AlarmType
import lowblow.annoying_alarm.data.alarm.AlarmFragmentData

abstract class FragmentParent : Fragment() {

    open fun getData(): AlarmFragmentData {
        return AlarmFragmentData(
            false,
            0F,
            AlarmType.FRAGMENT_CUSTOM,
            null,
            null
        )
    }
}