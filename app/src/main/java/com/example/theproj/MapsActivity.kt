package com.example.theproj

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.room.Room

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.theproj.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    //private val REQUEST_PERMISSION_LOCATION = 10
    lateinit var locationPermission: ActivityResultLauncher<Array<String>>

    lateinit var x: String
    lateinit var y: String
    lateinit var name: String
    lateinit var fac : String
    lateinit var loc : String

    private lateinit var mMap: GoogleMap

    val binding by lazy { ActivityMapsBinding.inflate(layoutInflater) }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        var intent = intent

        for (i in 1..2631) {
            x = intent.getStringExtra("get_X_$i").toString()
            y = intent.getStringExtra("get_Y_$i").toString()
            name = intent.getStringExtra("get_Name_$i").toString()
            fac = intent.getStringExtra("get_fac_$i").toString()
            loc = intent.getStringExtra("get_loc_$i").toString()

        }


        locationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { results ->
            if(results.all{ it.value }) {
                startProcess()
            } else {
                Toast.makeText(this
                    , "권한 승인이 필요합니다."
                    , Toast.LENGTH_LONG).show()
            }
        }

        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
        )
    }

    fun startProcess() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        updateLocation()
    }

    @SuppressLint("MissingPermission")
    fun updateLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0?.let {
                        for(location in it.locations){
                            Log.d("Location", "${location!!.latitude} , ${location!!.longitude}")
                            setLastLocation(location)
                        }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
            Looper.myLooper())
    }

    fun setLastLocation(lastLocation: Location) {
        val LATLNG = LatLng(lastLocation.latitude, lastLocation.longitude)

        var bitmapDrawable: BitmapDrawable

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bitmapDrawable = getDrawable(R.drawable.marker) as BitmapDrawable
        } else {
            bitmapDrawable = resources.getDrawable(R.drawable.marker) as BitmapDrawable
        }

        var discriptor = BitmapDescriptorFactory.fromBitmap(bitmapDrawable.bitmap)

        val markerOptions = MarkerOptions()
            .position(LATLNG)
            .title("내 현재 위치")
            .icon(discriptor)


        val cameraPosition = CameraPosition.Builder()
            .target(LATLNG)
            .zoom(10.0f)
            .build()
        mMap.clear()
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        var discriptor_park = BitmapDescriptorFactory.fromBitmap(bitmapDrawable.bitmap)


        val ParkDBTable = Room.databaseBuilder(this, AppDatabase::class.java, "db").build()

        CoroutineScope(Dispatchers.Main).launch {
            var latLng_Park : LatLng? = null

            var x_list = ParkDBTable.parkDBInterface().getX()
            var y_list = ParkDBTable.parkDBInterface().getY()
            var name_list = ParkDBTable.parkDBInterface().getName()

            for (i in (0..x_list.size-1)) {
                latLng_Park = LatLng(x_list[i].toDouble(), y_list[i].toDouble())
                Log.d("좌표는 ", "$latLng_Park")


                //고칠것 1
                if((lastLocation.latitude + (150000*2)/(110941 + 111034)) > x_list[i].toDouble() &&    //100은 m
                    (lastLocation.latitude - (150000*2)/(110941 + 111034)) < x_list[i].toDouble() &&
                    (lastLocation.longitude + (150000*2) / (91290 + 85397)) > y_list[i].toDouble() &&
                    (lastLocation.longitude - (150000*2) / (91290 + 85397)) < y_list[i].toDouble()) {
                    //내 위치를 기준으로 거리찾기


                    mMap.addMarker(MarkerOptions()
                        .position(latLng_Park)
                        .title(name_list[i])
                        .icon(discriptor_park)
                    )
                }
            }

            for (i in 1..name_list.size-1) {
                mMap.setOnMarkerClickListener(this@MapsActivity)
            }
        }


    }

    override fun onMarkerClick(p0: Marker): Boolean {
        if(p0.title.toString() == "내 현재 위치") {
            Toast.makeText(applicationContext, p0.title, Toast.LENGTH_SHORT).show()
        }else{
            var dlg = AlertDialog.Builder(this)
                .setTitle(p0.title)
                .setMessage("주요 시설 : ")
                .show()
        }
        return true
        //여기다가 클릭이벤트 작성
    }


}