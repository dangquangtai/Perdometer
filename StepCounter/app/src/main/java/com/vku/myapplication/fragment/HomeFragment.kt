package com.vku.myapplication.fragment

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.vku.myapplication.R
import com.vku.myapplication.database.*
import com.vku.myapplication.databinding.FragmentHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.log


class HomeFragment : Fragment(), SensorEventListener {
    lateinit var binding: FragmentHomeBinding
    lateinit var database: PedometerDatabaseDAO
    private var sensorManager: SensorManager? = null
    var running = false
    private lateinit var viewModel: HomeViewModel
    private val Initial_Count_Key = "FootStepInitialCount"
    private var sessionSteps = 0f
    var runing = false
    var isChanging = false
    var pauseOffset: Long = 0
    var stepLenghth: Int = 0
    var gender: Int = 1
    var age: Int = 1
    var height: Int = 0
    var weight: Int = 0
    var getSetbase: Boolean = false
    lateinit var database1: PersonalDatabaseDAO
    var myPersonalInfo: PersonalInfo? = null
    private var lastTimeSensor = 0L
    var newStepValue = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val application = requireNotNull(this.activity).application
        database = PedometerDatabase.getInstance(application).pedometerDatabaseDAO
        val viewModelFactory = HomeViewModelFactory(database, application, getDay())
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        binding.clearBtn.setOnClickListener {
            viewModel.onClear()
        }
        database1 = PersonalDatabase.getInstance(application).personalDatabaseDAO

        val personalInfo = database1.getListPersonalInfo()
        personalInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isNotEmpty()) {
                    val thisPersonalInfo = it[0]
                    myPersonalInfo = it[0]
                    stepLenghth = thisPersonalInfo.stepLength.toInt()
                    age = thisPersonalInfo.age.toInt()
                    weight = thisPersonalInfo.weight.toInt()
                    height = thisPersonalInfo.height.toInt()

                    if (thisPersonalInfo.sex.equals("female")) {
                        gender = 0
                    }

                } else {
                    myPersonalInfo = PersonalInfo()
                    viewLifecycleOwner.lifecycleScope.launch {
                        database1.insert(myPersonalInfo!!)
                    }
                    Toast.makeText(context, "null data", Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.pedometerAll.observe(viewLifecycleOwner, Observer {
            it?.let {


                if (it.isNotEmpty()) {

                    pauseOffset = it[0].countTime
                    Log.i("cxs",getSetbase.toString())
                    if (getSetbase == false) {
                        binding.chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset)
                        getSetbase = true
                    }
                    updateUI(it[0])
//                    updateClock(it[0])
                    viewModel.todayPedometer = it[0]
                } else {
                    val newPedometer = Pedometer()
                    newPedometer.day = getDay()
                    newPedometer.numberSteps = 0.0F
                    newPedometer.countTime = 0
                    newPedometer.caloriesBurned = 0f
                    newPedometer.distance = 0.0
                    newPedometer.speed = 0f
                    viewModel.onInsertToday(newPedometer)
                }
            }
        })


        viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                val x = System.currentTimeMillis() - lastTimeSensor
                Log.i("tag", x.toString())
                if (x > 2000L) {
                    pauseChronometer()
                    Log.i("tag", "Update data")
                    val oldPedometer = viewModel.todayPedometer
                    if (oldPedometer != null) {
                        oldPedometer.countTime = pauseOffset
                        if(pauseOffset == 0L){
                            oldPedometer.speed = 0f
                        }else{
                            oldPedometer.speed =
                                ((oldPedometer.distance) / ((pauseOffset) / (1000f))).toFloat()
                        }

                        oldPedometer.caloriesBurned = calculateEnergyExpenditure(
                            height,
                            age,
                            weight,
                            gender,
                            pauseOffset,
                            oldPedometer.distance.toFloat()
                        )
                        viewModel.onUpdate(oldPedometer)
                        updateUI(oldPedometer)
                    }

                }
                delay(1000)
            }
        }
        return binding.root
    }


//    override fun onStart() {
//        super.onStart()
//        running = true
//        var stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
//
//        if (stepsSensor == null) {
//            Toast.makeText(requireContext(), "No Step Counter Sensor !", Toast.LENGTH_SHORT)
//                .show()
//        } else {
//
//            sensorManager?.registerListener(
//                this,
//                stepsSensor,
//                SensorManager.SENSOR_DELAY_UI
//
//            )
//        }
//    }

    override fun onResume() {
        super.onResume()
        running = true
        var stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepsSensor == null) {
            Toast.makeText(requireContext(), "No Step Counter Sensor !", Toast.LENGTH_SHORT)
                .show()
        } else {
            sensorManager?.registerListener(
                this,
                stepsSensor,
                SensorManager.SENSOR_DELAY_UI


            )
        }
    }

    override fun onPause() {
        super.onPause()
        getSetbase = false
        Log.i("cxs", getSetbase.toString())
        sensorManager?.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
//        running = false
//        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            lastTimeSensor = System.currentTimeMillis()

            if (event != null) {
                Log.i("tag", "current stepValues = " + event.values[0])
                val prefs = context?.getSharedPreferences("session", Context.MODE_PRIVATE)
                if (prefs != null) {
                    newStepValue = if (event.values[0] < prefs.getFloat(Initial_Count_Key, 0f)) {
                        event.values[0]
                    } else {
                        event.values[0] - prefs.getFloat(Initial_Count_Key, 0f)
                    }

                    sessionSteps += newStepValue
                    val editor: SharedPreferences.Editor = prefs.edit()
                    editor.putFloat(Initial_Count_Key, event.values[0])
                    editor.commit()

//                    Timer("SettingUp", false).schedule(1500) {
//                        pauseChronometer()
//
//                    }
                }

            }
            if (viewModel.todayPedometer != null) {
                Log.i("tag", "Update data")
                val oldPedometer = viewModel.todayPedometer
                if (oldPedometer != null) {
                    startChronometer()
                    val elapsedMillis: Long =
                        SystemClock.elapsedRealtime() - binding.chronometer.base
                    oldPedometer.numberSteps += newStepValue
                    oldPedometer.distance += newStepValue * (stepLenghth / 100f)
                    viewModel.onUpdate(oldPedometer)
                    updateUI(oldPedometer)
                }
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun updateUI(pedometer: Pedometer) {
        try {
            binding.stepsValue.text = pedometer.numberSteps.toString()
            binding.distance.text =
                BigDecimal(pedometer.distance.toDouble()).setScale(2, RoundingMode.HALF_EVEN)
                    .toString() + " m"

            binding.speed.text =
                BigDecimal(pedometer.speed.toDouble()).setScale(2, RoundingMode.HALF_EVEN)
                    .toString() + " m/s"
            binding.calories.text =
                BigDecimal(pedometer.caloriesBurned.toDouble()).setScale(3, RoundingMode.HALF_EVEN)
                    .toString() + " Kcal"


//            binding.chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            Log.i("tag1", "test")
        } catch (e: Exception) {
            Log.e("tag", e.message.toString())
        }

    }

    private fun updateClock(pedometer: Pedometer) {
        try {

            pauseOffset = pedometer.countTime
            binding.chronometer.base = SystemClock.elapsedRealtime() - pauseOffset

        } catch (e: Exception) {
            Log.e("tag", e.message.toString())
        }

    }

    private fun getDay(): Long {
        val date = Date()
        val strDateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(strDateFormat)
        val todayDate = sdf.format(date)
        return sdf.parse(todayDate).time
    }

    private fun startChronometer() {
        if (!runing) {
//            val prefsChom = context?.getSharedPreferences("prefsChrom", Context.MODE_PRIVATE)
//            pauseOffset = prefsChom!!.getLong("pauseOffset", 0)

            binding.chronometer.start()
            runing = true
        }

    }

    private fun pauseChronometer() {
        if (runing) {

            binding.chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - binding.chronometer.base
//            val prefsChom = context?.getSharedPreferences("prefsChrom", Context.MODE_PRIVATE)
//            val editors = prefsChom!!.edit()
//            editors.putLong("pauseOffset", pauseOffset)
//            editors.apply()
            runing = false
        }

    }

    private fun harrisBenedictRmr(
        gender: Int,
        weightKg: Int,
        age: Int,
        heightCm: Int
    ): Float {
        return if (gender == 0) {
            655.0955f + 1.8496f * heightCm + 9.5634f * weightKg - 4.6756f * age
        } else {
            66.4730f + 5.0033f * heightCm + 13.7516f * weightKg - 6.7550f * age
        }
    }

    private fun convertKilocaloriesToMlKmin(kilocalories: Float, weightKgs: Float): Float {
        var kcalMin = kilocalories / 1440
        kcalMin /= 5f
        return kcalMin / weightKgs * 1000
    }

    private fun calculateEnergyExpenditure(
        height: Int,
        age: Int?,
        weight: Int,
        gender: Int,
        durationInSeconds: Long,
        distanceTravel: Float,

        ): Float {

        val harrisBenedictRmR = convertKilocaloriesToMlKmin(
            harrisBenedictRmr(gender, weight, age!!, height),
            weight.toFloat()
        )
        val kmTravelled: Float = distanceTravel / 1000f
        val hours: Float = (durationInSeconds / 1000f) / 3600f
        val speedInMph: Float = kmTravelled * 0.62137f
        val metValue: Float = getMetForActivity(speedInMph)
        val constant = 3.5f
        val correctedMets = metValue * (constant / harrisBenedictRmR)



        return correctedMets * hours * weight
    }

    private fun getMetForActivity(speedInMph: Float): Float {
        if (speedInMph < 2.0) {
            return 2.0f
        } else if (java.lang.Float.compare(speedInMph, 2.0f) == 0) {
            return 2.8f
        } else if (java.lang.Float.compare(speedInMph, 2.0f) > 0 && java.lang.Float.compare(
                speedInMph,
                2.7f
            ) <= 0
        ) {
            return 3.0f
        } else if (java.lang.Float.compare(speedInMph, 2.8f) > 0 && java.lang.Float.compare(
                speedInMph,
                3.3f
            ) <= 0
        ) {
            return 3.5f
        } else if (java.lang.Float.compare(speedInMph, 3.4f) > 0 && java.lang.Float.compare(
                speedInMph,
                3.5f
            ) <= 0
        ) {
            return 4.3f
        } else if (java.lang.Float.compare(speedInMph, 3.5f) > 0 && java.lang.Float.compare(
                speedInMph,
                4.0f
            ) <= 0
        ) {
            return 5.0f
        } else if (java.lang.Float.compare(speedInMph, 4.0f) > 0 && java.lang.Float.compare(
                speedInMph,
                4.5f
            ) <= 0
        ) {
            return 7.0f
        } else if (java.lang.Float.compare(speedInMph, 4.5f) > 0 && java.lang.Float.compare(
                speedInMph,
                5.0f
            ) <= 0
        ) {
            return 8.3f
        } else if (java.lang.Float.compare(speedInMph, 5.0f) > 0) {
            return 9.8f
        }
        return 0f

    }

}