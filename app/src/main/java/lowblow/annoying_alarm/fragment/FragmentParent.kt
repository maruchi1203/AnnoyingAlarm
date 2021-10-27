package lowblow.annoying_alarm.fragment

import androidx.fragment.app.Fragment
import lowblow.annoying_alarm.data.AlarmType
import lowblow.annoying_alarm.data.alarm.AlarmFragmentData

abstract class FragmentParent : Fragment() {

    open fun getData() : AlarmFragmentData {
        return AlarmFragmentData(
            null,
            false,
            0.toFloat(),
            AlarmType.FRAGMENT_CUSTOM
        )
    }

    open fun setData(data : AlarmFragmentData) {

    }

}