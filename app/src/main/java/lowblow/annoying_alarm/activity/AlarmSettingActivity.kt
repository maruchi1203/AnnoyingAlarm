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
import kotlin.coroutines.coroutineContext
import kotlin.math.pow

class AlarmSettingActivity() : AppCompatActivity() {

    private val binding by lazy {
        AlarmSettingBinding.inflate(layoutInflater)
    }

    private var alarmEntity: AlarmEntity? = null

    //타임피커
    private lateinit var timePicker: TimePicker
    private var time = null
    private var is24Hour = false

    //요일
    private var daysCheckBox: MutableList<CheckBox> = mutableListOf()

    //요일 표시 스트링
    private lateinit var daysTextView: TextView

    //요일 표시 저장 스트링
    private var daysSave: Int = 0

    //스피너
    private lateinit var alarmSpinner: Spinner

    //저장 버튼
    private lateinit var alarmSaveButton: AppCompatButton

    //프래그먼트 통제용 INT
    private var fragmentPos = 0

    //프래그먼트 불러오기
    private var fragment: FragmentParent = FragmentCustom()

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
        }

        initViews()

    }

    private fun bindViews() {
        timePicker = binding.alarmTimePicker

        for (i in 0..binding.daysCheckBoxList.childCount - 1) {
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
        if(alarmEntity != null) {
            daysSave = alarmEntity!!.days
        }

        for (i in 0 until daysCheckBox.count()) {
            val daysBit = (2.0).pow(i).toInt()

            if ((daysSave and daysBit) != 0) {
                daysCheckBox[i].isChecked = true
            }

            daysCheckBox[i].setOnCheckedChangeListener { view, isChecked ->
                //월요일부터 1, 2, 4... (비트마스크)로 날짜 구별


                if (Build.VERSION_CODES.M > Build.VERSION.SDK_INT) {
                    if (isChecked) {

                        view.setTextColor(resources.getColor(R.color.white))
                    } else {

                        view.setTextColor(resources.getColor(R.color.black))
                    }
                } else {
                    if (isChecked) {
                        view.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.white
                            )
                        )
                    } else {
                        view.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.black
                            )
                        )
                    }
                }

                if (isChecked) {
                    view.setBackgroundResource(R.drawable.button_selected)
                    daysSave += daysBit
                } else {
                    view.setBackgroundResource(R.drawable.button_unselected)
                    daysSave = daysBit
                }
            }
        }
    }

    private fun initAlarmSpinner() {
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(R.id.selectedModeFragmentView, FragmentCustom())
            .commitAllowingStateLoss()

        val spinnerItems = resources.getStringArray(R.array.alarm_theme)
        val spinnerAdapter =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerItems)

        alarmSpinner.adapter = spinnerAdapter

        alarmSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val repeatFragmentManager = supportFragmentManager.beginTransaction()

                when (position) {
                    0 -> {
                        if (fragmentPos != 0) {
                            fragment = FragmentCustom()
                            repeatFragmentManager.replace(R.id.selectedModeFragmentView, fragment)
                                .commitAllowingStateLoss()
                            fragmentPos = 0
                        }
                    }
                    1 -> {
                        if (fragmentPos != 1) {
                            fragment = FragmentMosquito()
                            repeatFragmentManager.replace(R.id.selectedModeFragmentView, fragment)
                                .commitAllowingStateLoss()
                            fragmentPos = 1
                        }
                    }
                    2 -> {
                        if (fragmentPos != 2) {
                            fragment = FragmentSiren()
                            repeatFragmentManager.replace(R.id.selectedModeFragmentView, fragment)
                                .commitAllowingStateLoss()
                            fragmentPos = 2
                        }
                    }
                    3 -> {
                        if (fragmentPos != 3) {
                            fragment = FragmentMessenger()
                            repeatFragmentManager.replace(R.id.selectedModeFragmentView, fragment)
                                .commitAllowingStateLoss()
                            fragmentPos = 3
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
    }

    private fun initSaveButton() {
        alarmSaveButton.setOnClickListener { _ ->
            val entityId: Long = intent.getLongExtra("id", 0)
            val data = fragment.getData()

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
                        data.loudness,
                        data.gentleAlarm,
                        binding.alarmMemoEditText.text.toString(),
                        data.mode
                    )
                )

            } else {

                DataController(this).alarmDataUpdate(
                    AlarmEntity(
                        id = entityId,
                        activated = true,
                        hour = timePicker.hour,
                        minute = timePicker.minute,
                        days = daysSave,
                        alarmUri = data.alarmUri,
                        vibration = data.vibration,
                        loudness = data.loudness,
                        gentleAlarm = data.gentleAlarm,
                        memo = binding.alarmMemoEditText.text.toString(),
                        mode = data.mode
                    )
                )

            }

            finish()
        }
    }
}