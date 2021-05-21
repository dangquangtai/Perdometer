package com.vku.myapplication.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vku.myapplication.R
import com.vku.myapplication.fragment.DataFragment
import com.vku.myapplication.fragment.DirectionFragment
import com.vku.myapplication.fragment.HomeFragment
import com.vku.myapplication.fragment.UserInfoFragment

class MainActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment()
    private val fragmentManager = supportFragmentManager
    private val dataFragment = DataFragment()
    private val infoFragment = UserInfoFragment()
    private var activeFragment: Fragment = homeFragment
    private var directionFragment = DirectionFragment()
    private var locationPermissionGranted = false
    val appPermission = listOf(
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, homeFragment, "home")
            add(R.id.fragment_container, dataFragment, "data").hide(dataFragment)
            add(R.id.fragment_container, infoFragment, "data").hide(infoFragment)
            add(R.id.fragment_container, directionFragment, "direction").hide(directionFragment)
        }.commit()
        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.nav_home
        bottomNavigationView.setOnNavigationItemSelectedListener(
            object : BottomNavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.nav_home -> {
                            fragmentManager.beginTransaction().hide(activeFragment)
                                .show(homeFragment).commit()
                            activeFragment = homeFragment
                            return true
                        }
                        R.id.nav_data -> {
                            fragmentManager.beginTransaction().hide(activeFragment)
                                .show(dataFragment).commit()
                            activeFragment = dataFragment
                            return true
                        }
                        R.id.nav_direction -> {
                            fragmentManager.beginTransaction().hide(activeFragment)
                                .show(directionFragment).commit()
                            activeFragment = directionFragment
                            return true
                        }
                        R.id.nav_info -> {
                            fragmentManager.beginTransaction().hide(activeFragment)
                                .show(infoFragment).commit()
                            activeFragment = infoFragment
                            return true
                        }
                    }
                    return false
                }
            })
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions(): Boolean {
        val listPermissionNeeded = ArrayList<String>()
        for (perm in appPermission) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionNeeded.add(perm)
            }
        }
        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionNeeded.toTypedArray(),
                999
            )
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 999) {
            if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                if ((ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ) ===
                            PackageManager.PERMISSION_GRANTED)
                ) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }

                if ((ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) ===
                            PackageManager.PERMISSION_GRANTED)
                ) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            return
        }
    }
}