package com.vku.myapplication.activity

import android.Manifest
import android.app.Service
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.vku.myapplication.R
import com.vku.myapplication.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.fragment_direction.*
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.security.Provider
import kotlin.reflect.KParameter


class MainActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment()
    private val fragmentManager = supportFragmentManager
    private val dataFragment = DataFragment()
    private val infoFragment = UserInfoFragment()
    private var activeFragment: Fragment = homeFragment
    private var directionFragment = DirectionFragment()
    private var newsFragment = NewsFragment()
    private var locationPermissionGranted = false

    //    lateinit var btnStart:Button
    lateinit var konfettiView: KonfettiView
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
            add(R.id.fragment_container, infoFragment, "info").hide(infoFragment)
            add(R.id.fragment_container, directionFragment, "direction").hide(directionFragment)
            add(R.id.fragment_container, newsFragment, "news").hide(newsFragment)
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
                        R.id.nav_news -> {
                            fragmentManager.beginTransaction().hide(newsFragment)
                                .show(newsFragment).commit()
                            activeFragment = newsFragment
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

    override fun onBackPressed() {
        if (wb_webView.canGoBack()) wb_webView.goBack() else super.onBackPressed()
    }

//    fun firework() {
//        setContentView(R.layout.fragment_direction)

//        btnStart =findViewById(R.id.btnStart)
//        btnStart.setOnClickListener(View.OnClickListener {
//            konfettiView.build()
//                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
//                .setDirection(0.0, 359.0)
//                .setSpeed(1f, 5f)
//                .setFadeOutEnabled(true)
//                .setTimeToLive(2000L)
//                .addShapes(Shape.Square, Shape.Circle)
//                .addSizes(Size(12))
//                .setPosition(-50f, konfettiView.width + 50f, -50f, -50f)
//                .streamFor(300, 5000L)
//        })
//    }


}