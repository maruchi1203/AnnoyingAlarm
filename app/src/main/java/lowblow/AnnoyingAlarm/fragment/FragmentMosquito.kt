package lowblow.AnnoyingAlarm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lowblow.AnnoyingAlarm.R

class FragmentMosquito: FragmentParent() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mosquito, null)
    }

}