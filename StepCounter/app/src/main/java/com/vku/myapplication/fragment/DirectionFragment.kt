package com.vku.myapplication.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.vku.myapplication.R
import com.vku.myapplication.map.DirectionResponses
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigDecimal
import java.math.RoundingMode

class DirectionFragment : Fragment() {
    private lateinit var btn_find: Button
    private lateinit var btn_start: Button
    private lateinit var edt_address: EditText
    private lateinit var textDistance: TextView
    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationUpdatesCallback: LocationCallback? = null
    private lateinit var locationRequest: LocationRequest
    private var isStart = false
    lateinit var map: GoogleMap
    private val listLocation = ArrayList<LatLng>()
    private var sumDistance = 0.0

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // Set the map's camera position to the current location of the device.
                lastKnownLocation = task.result
                if (lastKnownLocation != null) {
                    googleMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude
                            ), 17F
                        )
                    )
                    googleMap?.isMyLocationEnabled = true
                    googleMap?.uiSettings?.isMyLocationButtonEnabled = true
                }
            }
        }
        val apiServices = RetrofitClient.apiServices()

        btn_find.setOnClickListener {
            val location = edt_address.text.toString()
            if (location.isNotEmpty()) {
                var addressList: List<Address> = ArrayList()
                val geoCoder = Geocoder(requireContext())
                try {
                    addressList = geoCoder.getFromLocationName(location, 1)
                } catch (e: Exception) {
                }
                if (addressList.isNotEmpty() && lastKnownLocation != null) {
                    val address = addressList[0]
                    val from =
                        lastKnownLocation!!.latitude.toString() + "," + lastKnownLocation!!.longitude.toString()
                    val end = address.latitude.toString() + "," + address.longitude.toString()
                    apiServices.getDirection(
                        from,
                        location,
                        "AIzaSyDcvQwgiyBLQc4Dvad4cJZZimf7C4z6r1o"
                    )
                        .enqueue(object : Callback<DirectionResponses> {
                            override fun onResponse(
                                call: Call<DirectionResponses>,
                                response: Response<DirectionResponses>
                            ) {
                                drawPolyline(response, googleMap)

                            }

                            override fun onFailure(
                                call: Call<DirectionResponses>,
                                t: Throwable
                            ) {

                            }
                        })
                }
            }


        }
        btn_start.setOnClickListener {
            if (lastKnownLocation != null) {
                if (!isStart) {
                    googleMap.clear()
                    listLocation.clear()
                    btn_start.text = "Stop"
                    sumDistance = 0.0
                    isStart = true
                } else {
                    btn_start.text = "Start"
                    isStart = false
                }
            } else {
                Toast.makeText(context, "Not known your location!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_direction, container, false)
        btn_find = root.findViewById(R.id.btn_find)
        btn_start = root.findViewById(R.id.btnStart)
        edt_address = root.findViewById(R.id.edt_address)
        textDistance = root.findViewById(R.id.textDistance)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        setUpLocationRequest()
        setUpLocationUpdatesCallback()
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationUpdatesCallback,
            null
        )
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationUpdatesCallback)
        super.onDestroy()
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationUpdatesCallback)
        super.onStop()
    }

    private fun drawPolyline(response: Response<DirectionResponses>, map: GoogleMap) {
        if (response.body().routes?.size!! > 0) {
            val leg = response.body().routes?.get(0)?.legs?.get(0)
            val routeList = response.body()?.routes
            if (routeList != null) {
                for (route in routeList) {
                    val shape = route?.overviewPolyline?.points
                    val polyline = PolylineOptions()
                        .addAll(PolyUtil.decode(shape))
                        .width(9f)
                        .color(Color.BLUE)
                    val line = map.addPolyline(polyline)
                }
            }

            val marker = MarkerOptions().position(
                LatLng(
                    leg?.endLocation?.lat!!, leg?.endLocation?.lng!!
                )
            ).title(leg.endAddress)
                .icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            val newMarker = map.addMarker(marker)
            newMarker.showInfoWindow()
        }

    }

    private interface ApiServices {
        @GET("maps/api/directions/json")
        fun getDirection(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("key") apiKey: String
        ): Call<DirectionResponses>
    }

    private object RetrofitClient {
        fun apiServices(): DirectionFragment.ApiServices {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://maps.googleapis.com")
                .build()
            return retrofit.create<DirectionFragment.ApiServices>(DirectionFragment.ApiServices::class.java)
        }
    }

    private fun setUpLocationUpdatesCallback() {
        locationUpdatesCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult != null) {
                    lastKnownLocation = locationResult.lastLocation
                    if (isStart) {

                        if (listLocation.size > 0) {
                            val lc = Location("")
                            lc.latitude = listLocation.last().latitude
                            lc.longitude = listLocation.last().longitude
                            val tempDistance = lc?.distanceTo(lastKnownLocation)
                            Log.i("tag", "distace $tempDistance")
                            if (tempDistance > 1.0) {
                                sumDistance += tempDistance
                                textDistance.text = "Distance: " + BigDecimal(sumDistance).setScale(
                                    2,
                                    RoundingMode.HALF_EVEN
                                ).toString() + "m"
                            }
                        }
                        listLocation.add(
                            LatLng(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude
                            )
                        )
                        map.clear()
                        val polyline1 = map.addPolyline(
                            PolylineOptions()
                                .clickable(true)
                                .addAll(listLocation)
                        )
                        polyline1.color = Color.GREEN
                        polyline1.width = 8f

                    }
                    Log.i("tag", "update location")
                } else {
                    Log.i("tag", "Location null")
                }
            }
        }
    }

    private fun setUpLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

}