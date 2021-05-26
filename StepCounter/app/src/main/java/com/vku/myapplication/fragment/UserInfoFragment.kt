package com.vku.myapplication.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.vku.myapplication.R
import com.vku.myapplication.database.*
import com.vku.myapplication.databinding.FragmentDataBinding
import com.vku.myapplication.databinding.FragmentUserInfoBinding
import kotlinx.android.synthetic.main.fragment_user_info.*
import kotlinx.android.synthetic.main.fragment_user_info.view.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class UserInfoFragment : Fragment() {
    lateinit var binding: FragmentUserInfoBinding
    lateinit var database: PersonalDatabaseDAO
    var myPersonalInfo: PersonalInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_info, container, false)
        val application = requireNotNull(this.activity).application
        binding.lifecycleOwner = this
        database = PersonalDatabase.getInstance(application).personalDatabaseDAO
        val personalInfo = database.getListPersonalInfo()
        personalInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isNotEmpty()) {
                    val thisPersonalInfo = it[0]
                    myPersonalInfo = it[0]
                    binding.inputAge.setText("" + thisPersonalInfo.age)
                   if (thisPersonalInfo.sex == "male"){
                     binding.radioGroup.maleChecked.isChecked =true
                   }else{
                       binding.radioGroup.femaleChecked.isChecked =true
                   }
                    binding.inputWeight.setText("" + thisPersonalInfo.weight)
                    binding.inputStepLength.setText("" + thisPersonalInfo.stepLength)
                    binding.inputHeight.setText("" + thisPersonalInfo.height)
                } else {
                    myPersonalInfo = PersonalInfo()
                    viewLifecycleOwner.lifecycleScope.launch {
                        database.insert(myPersonalInfo!!)
                    }
                    Toast.makeText(context, "null data", Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.btnConfirm.setOnClickListener {
            if (myPersonalInfo != null) {
         if (radioGroup.maleChecked.isChecked ==true){
             myPersonalInfo!!.sex ="male"
         }  else {
             myPersonalInfo!!.sex ="female"
         }
                Toast.makeText(context, myPersonalInfo!!.sex.toString(), Toast.LENGTH_SHORT).show()
                myPersonalInfo!!.weight = binding.inputWeight.text.toString()
                myPersonalInfo!!.stepLength = binding.inputStepLength.text.toString()
                myPersonalInfo!!.age = binding.inputAge.text.toString()
                myPersonalInfo!!.height = binding.inputHeight.text.toString()
                viewLifecycleOwner.lifecycleScope.launch {
                    database.update(myPersonalInfo!!)
                }
                Toast.makeText(context, "Update Succeed !", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

}