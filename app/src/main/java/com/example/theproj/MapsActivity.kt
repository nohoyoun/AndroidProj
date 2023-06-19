package com.example.theproj

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import com.google.android.gms.maps.model.CircleOptions
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
    var getX : Float = 0.0f
    var getY : Float = 0.0f
    lateinit var  getName : String
    var switch_Search : Int = 0


    private lateinit var mMap: GoogleMap

    val binding by lazy { ActivityMapsBinding.inflate(layoutInflater) }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        var intent = intent

        switch_Search = intent.getIntExtra("switch", 0)


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
            interval = 50000
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
        var bitmapPark : BitmapDrawable

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bitmapDrawable = getDrawable(R.drawable.marker) as BitmapDrawable
            bitmapPark = getDrawable(R.drawable.park_mark) as BitmapDrawable
        } else {
            bitmapDrawable = resources.getDrawable(R.drawable.marker) as BitmapDrawable
            bitmapPark = resources.getDrawable(R.drawable.park_mark) as BitmapDrawable
        }

        var discriptor = BitmapDescriptorFactory.fromBitmap(bitmapDrawable.bitmap)

        val markerOptions = MarkerOptions()
            .position(LATLNG)
            .title("내 현재 위치")
            .icon(discriptor)

        val cameraPosition = CameraPosition.Builder()
            .target(LATLNG)
            .zoom(12.0f)
            .build()
        mMap.clear()
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        //내 위치

        if(switch_Search == 0) {
            parkLocation(lastLocation, bitmapPark)
        }else{
            bySearchAct(markerOptions, bitmapPark)
        }

    }

    override fun onMarkerClick(p0: Marker): Boolean {
        val ParkDBTable = Room.databaseBuilder(this, AppDatabase::class.java, "db").build()

        if(p0.title.toString() == "내 현재 위치") {
            Toast.makeText(applicationContext, p0.title, Toast.LENGTH_SHORT).show()
        }else{
            var fac = SeeFac(p0.title.toString(), ParkDBTable)
            Log.d("장소이름 : ", fac)
            var dlg = AlertDialog.Builder(this)
                .setTitle(p0.title)
                .setMessage("주요 시설\n" + fac)
                .setNegativeButton("닫기", DialogInterface.OnClickListener { dialog, which ->
                })
                .show()
        }
        return true
        //여기다가 클릭이벤트 작성
    }

    //편의시설 출력함수
    fun SeeFac(strid : String, ParkDBTable: AppDatabase) : String {
            CoroutineScope(Dispatchers.Main).launch {
                Log.d("strid", strid.toString())
                fac = ParkDBTable.parkDBInterface().getfacbyName(strid)
                Log.d("SeeList fac출력", fac)
                if(fac == null && fac == "null") {
                    fac = ""
                }
        }
        return fac
    }

    //거리계산 함수
    fun distancebyDegree(_latitude1 : Double, _longitude1 : Double, _latitude2 : Double, _longitude2 : Double) : Float {
        var startPos = Location("PointA");
        var endPos = Location("PointB");

        startPos.setLatitude(_latitude1);
        startPos.setLongitude(_longitude1);
        endPos.setLatitude(_latitude2);
        endPos.setLongitude(_longitude2);

        var distance = startPos.distanceTo(endPos);

        return distance
    }

    fun parkLocation(lastLocation: Location, bitmapPark : BitmapDrawable) {

        var discriptor_park = BitmapDescriptorFactory.fromBitmap(bitmapPark.bitmap)
        val ParkDBTable = Room.databaseBuilder(this, AppDatabase::class.java, "db").build()

        CoroutineScope(Dispatchers.Main).launch {
            var latLng_Park : LatLng? = null

            var x_list = ParkDBTable.parkDBInterface().getX()
            var y_list = ParkDBTable.parkDBInterface().getY()
            var name_list = ParkDBTable.parkDBInterface().getName()

            for (i in (0..x_list.size-1)) {
                latLng_Park = LatLng(x_list[i].toDouble(), y_list[i].toDouble())
                Log.d("좌표는 ", "$latLng_Park")

                //내 위치를 기준으로 거리찾기
                var MytoPark = distancebyDegree(lastLocation.latitude, lastLocation.longitude, x_list[i].toDouble(), y_list[i].toDouble())

                if(MytoPark < 10000) {
                    mMap.addMarker(MarkerOptions()
                        .position(latLng_Park)
                        .title(name_list[i])
                        .icon(discriptor_park)
                    )
                }
            }

            for (i in 1..name_list.size-1) {
                //mMap.setOnMarkerClickListener(this@MapsActivity)
            }
        }
    }

    fun bySearchAct(markerOptions: MarkerOptions, bitmapPark: BitmapDrawable) {
        var discriptor_park = BitmapDescriptorFactory.fromBitmap(bitmapPark.bitmap)
        getX = intent.getFloatExtra("getXbyS", 0.0f)
        getY = intent.getFloatExtra("getYbyS", 0.0f)
        getName = intent.getStringExtra("getNamebyS").toString()
        Log.d("getX", getX.toString())
        Log.d("getY", getY.toDouble().toString())


        var latLng_Park = LatLng(getX.toDouble(), getY.toDouble())
        mMap.addMarker(MarkerOptions()
            .position(latLng_Park)
            .title(getName)
            .icon(discriptor_park)
        )

        val cameraPosition = CameraPosition.Builder()
            .target(latLng_Park)
            .zoom(8.0f)
            .build()
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

    }

}