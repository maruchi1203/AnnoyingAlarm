package lowblow.annoying_alarm.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lowblow.annoying_alarm.R
import lowblow.annoying_alarm.data.alarm.AlarmEntity
import lowblow.annoying_alarm.databinding.AlarmSettingBinding
import lowblow.annoying_alarm.fragment.*
import lowblow.annoying_alarm.system_manager.DataController
import lowblow.annoying_alarm.system_manager.PreferenceManager
import kotlin.math.pow

class AlarmSettingActivity : AppCompatActivity() {

    //For updating alarm
    private var alarmEntity: AlarmEntity? = null

    //binding
    private val binding by lazy { AlarmSettingBinding.inflate(layoutInflater) }
    private lateinit var timePicker: TimePicker
    private lateinit var alarmSpinner: Spinner
    private lateinit var alarmSaveButton: AppCompatButton

    //Days
    private var daysCheckBox: MutableList<CheckBox> = mutableListOf()

    //(BitMask) For alternating daysSave = List<boolean>
    private var daysSave: Int = 0

    //Fragment value
    private var fragmentPos = 0
    private var initializedSpinner = false
    private val fragmentManager = supportFragmentManager

    //PreferenceValue
    private val is24Hour by lazy {
        PreferenceManager(baseContext).getBoolean("is24hour")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bindViews()

        CoroutineScope(Dispatchers.Main).launch {
            alarmEntity = withContext(Dispatchers.IO) {
                DataController(this@AlarmSettingActivity).getAlarmData(
                    intent.getLongExtra(
                        "id",
                        0
                    )
                )
            }
            initViews()

        }
    }

    private fun bindViews() {
        timePicker = binding.alarmTimePicker

        for (i in 0 until binding.daysCheckBoxList.childCount) {
            daysCheckBox.add(i, binding.daysCheckBoxList.getChildAt(i) as CheckBox)
        }

        alarmSpinner = binding.alarmChangeSpinner

        alarmSaveButton = binding.saveButton
    }

    private fun initViews() {
        initTimePicker()
        initDaysList()
        initAlarmSpinner()
        initSaveButton()
    }

    private fun initTimePicker() {
        timePicker.setIs24HourView(is24Hour)

        if (alarmEntity != null) {
            timePicker.hour = alarmEntity!!.hour
            timePicker.minute = alarmEntity!!.minute
        }
    }

    private fun initDaysList() {
        alarmEntity?.let { daysSave = it.days }

        for (i in 0 until daysCheckBox.count()) {
            val daysBit = (2.0).pow(i).toInt()

            if ((daysSave and daysBit) != 0) {
                daysCheckBox[i].isChecked = true
                changeCheckBoxColor(true, daysCheckBox[i])
            }

            daysCheckBox[i].setOnCheckedChangeListener { view, isChecked ->
                //월요일부터 1, 2, 4... (비트마스크)로 날짜 구별
                changeCheckBoxColor(isChecked, daysCheckBox[i])

                if (isChecked) {
                    view.setBackgroundResource(R.drawable.button_selected)
                    daysSave += daysBit
                } else {
                    view.setBackgroundResource(R.drawable.button_unselected)
                    daysSave -= daysBit
                }
            }
        }
    }

    private fun changeCheckBoxColor(isChecked: Boolean, view: CompoundButton) {
        if (Build.VERSION_CODES.M > Build.VERSION.SDK_INT) {
            if (isChecked) {
                view.setTextColor(resources.getColor(R.color.white))
                view.setBackgroundResource(R.drawable.button_selected)
            } else {
                view.setTextColor(resources.getColor(R.color.black))
                view.setBackgroundResource(R.drawable.button_unselected)
            }
        } else {
            if (isChecked) {
                view.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
                view.setBackgroundResource(R.drawable.button_selected)
            } else {
                view.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
                view.setBackgroundResource(R.drawable.button_unselected)
            }
        }
    }

    private fun initAlarmSpinner() {
        //Initialize value for fragmentManager
        alarmEntity?.let {
            fragmentPos = it.alarmType.ordinal
        }
        val spinnerItems = resources.getStringArray(R.array.alarm_theme)
        val spinnerAdapter =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerItems)
        val fragmentArray =
            arrayListOf(FragmentCustom(), FragmentMosquito(), FragmentSiren(), FragmentMessenger())

        alarmSpinner.adapter = spinnerAdapter

        alarmSpinner.setSelection(fragmentPos)

        fragmentManager.beginTransaction().replace(
            R.id.selectedModeFragmentView,
            fragmentArray[fragmentPos],
            "fragment"
        )
            .commitAllowingStateLoss()

        alarmSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (fragmentPos != position) {
                    fragmentManager.beginTransaction().replace(
                        R.id.selectedModeFragmentView,
                        fragmentArray[position],
                        "fragment"
                    )
                        .commitAllowingStateLoss()
                    fragmentPos = position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initSaveButton() {
        alarmSaveButton.setOnClickListener { _ ->
            val entityId: Long = intent.getLongExtra("id", 0)
            val data = (fragmentManager.findFragmentByTag("fragment") as FragmentParent).getData()

            if (entityId == 0.toLong()) {
                DataController(this).alarmDataCreate(
                    AlarmEntity(
                        0,
                        true,
                        timePicker.hour,
                        timePicker.minute,
                        daysSave,
                        data.alarmUri,
                        data.vibration,
                        data.volume,
                        binding.alarmMemoEditText.text.toString(),
                        data.alarmType,
                        false
                    )
                )

            } else {
                DataController(this).alarmDataUpdate(
                    AlarmEntity(
                        entityId,
                        true,
                        timePicker.hour,
                        timePicker.minute,
                        daysSave,
                        data.alarmUri,
                        data.vibration,
                        data.volume,
                        binding.alarmMemoEditText.text.toString(),
                        data.alarmType,
                        false
                    )
                )
            }

            finish()
        }
    }
}