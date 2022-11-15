package com.example.storyapp.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.model.StoryModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson

class MapStoryActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mutableListStoryModel: ArrayList<StoryModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_story)


        mutableListStoryModel = intent.getParcelableArrayListExtra<StoryModel>("stories") as ArrayList<StoryModel>

        Log.e("TAG", "onCreate: ${Gson().toJson(mutableListStoryModel)}")
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.map_story)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getAllLocation()
    }
    private fun getAllLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            for(story in mutableListStoryModel){
                if(story.lat!=0.0 && story.lon !=0.0){
                    mMap.addMarker(MarkerOptions().position(LatLng(story.lat,story.lon)).title(story.name).snippet(story.description))
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    //Precise location access granted
                    getAllLocation()
                }
                it[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    //Only approximate location access granted
                    getAllLocation()
                }
                else -> {


                }
            }

        }

}