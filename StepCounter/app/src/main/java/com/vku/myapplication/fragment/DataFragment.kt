package com.vku.myapplication.fragment

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
import com.vku.myapplication.R
import com.vku.myapplication.database.PedometerDatabase
import com.vku.myapplication.database.PedometerDatabaseDAO
import com.vku.myapplication.databinding.FragmentDataBinding
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class DataFragment : Fragment() {

    lateinit var binding: FragmentDataBinding
    lateinit var database: PedometerDatabaseDAO
    private lateinit var viewModel: DataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_data, container, false)
        val application = requireNotNull(this.activity).application
        database = PedometerDatabase.getInstance(application).pedometerDatabaseDAO
        val viewModelFactory = DataViewModelFactory(database, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DataViewModel::class.java)
        viewModel.onGetData(getStartWeekDay(), getEndWeekDay())
        //1621036800000  1621382400000
        updateUI()
        binding.week.setOnClickListener {
            viewModel.onGetData(getStartWeekDay(), getEndWeekDay())
            binding.textCurrentSelect.text = "Week"
            updateUI()
            Toast.makeText(context, "Get data of week!", Toast.LENGTH_SHORT).show()
        }
        binding.today.setOnClickListener {
            viewModel.onGetToday(getDay())
            binding.textCurrentSelect.text = "Today"
            updateUI()
            Toast.makeText(context, "Get data of Today!", Toast.LENGTH_SHORT).show()
        }
        binding.month.setOnClickListener {
            viewModel.onGetData(getStartMonthDay(), getEndMonthDay())
            binding.textCurrentSelect.text = "Month"
            updateUI()
            Toast.makeText(context, "Get data of month!", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    private fun updateUI() {
        viewModel.getAllPedometer?.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isNotEmpty()) {
                    var steps = 0f
                    var distance = 0.0
                    var speed = 0f
                    var calories = 0f
                    for (x in it) {
                        steps += x.numberSteps
                        distance += x.distance
                        speed = x.speed
                        calories += x.caloriesBurned
                        binding.chronometer.setBase(SystemClock.elapsedRealtime() - x.countTime + 400L)
                        Log.i("tag", "day " + convertLongToTime(x.day))
                    }
                    try {
                    binding.distance.text =
                        BigDecimal(distance.toDouble()).setScale(2, RoundingMode.HALF_EVEN)
                            .toString() + " m"

                    binding.speed.text =
                        BigDecimal(speed.toDouble()).setScale(2, RoundingMode.HALF_EVEN)
                            .toString() + " m/s"
                    binding.calories.text =
                        BigDecimal(calories.toDouble()).setScale(3, RoundingMode.HALF_EVEN)
                            .toString() + " Kcal"

                    binding.stepsValue.text = steps.toString()
                    } catch (e: Exception) {
                        Log.e("tag", e.message.toString())
                    }
                }
            }
        })
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

    private fun getStartWeekDay(): Long {
        val c1 = Calendar.getInstance()
        c1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        Log.i("tag", "getStartWeekDay=" + c1.time.toString())
        return convertDateToLong(c1.timeInMillis)
    }

    private fun getEndWeekDay(): Long {
        val c1 = Calendar.getInstance()
        c1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        Log.i("tag", "getEndWeekDay=" + c1.time.toString())
        return convertDateToLong(c1.timeInMillis)
    }

    private fun getStartMonthDay(): Long {
        val c1 = Calendar.getInstance()
        c1.set(Calendar.DAY_OF_MONTH, 1)
        Log.i("tag", "getStartMonthDay=" + c1.time.toString())
        return convertDateToLong(c1.timeInMillis)
    }

    private fun getEndMonthDay(): Long {
        val c1 = Calendar.getInstance()
        c1.set(Calendar.DAY_OF_MONTH, 28)
        Log.i("tag", "getEndMonthDay=" + c1.time.toString())
        return convertDateToLong(c1.timeInMillis)
    }

    private fun getDay(): Long {
        val date = Date()
        val strDateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(strDateFormat)
        val todayDate = sdf.format(date)
        return sdf.parse(todayDate).time
    }

    private fun convertDateToLong(param: Long): Long {
        val date = Date(param)
        val strDateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(strDateFormat)
        val todayDate = sdf.format(date)
        return sdf.parse(todayDate).time
    }
}
