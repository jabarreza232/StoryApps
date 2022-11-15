package com.example.storyapp.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.Utils.Companion.bitmapToFile
import com.example.storyapp.Utils.Companion.reduceFileImage
import com.example.storyapp.Utils.Companion.rotateBitmap
import com.example.storyapp.Utils.Companion.uriToFile
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.model.UserModel
import com.example.storyapp.preference.UserPreference
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.repository.UserRepository
import com.example.storyapp.viewmodel.CallBackResponse
import com.example.storyapp.viewmodel.LoginViewModel
import com.example.storyapp.viewmodel.StoryViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity(), CallBackResponse, OnMapReadyCallback {
    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var userModel: UserModel
    private var startLocation: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    companion object {
        const val CAMERA_X_RESULT = 200
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userRepository = UserRepository(UserPreference.getInstance(dataStore))

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        storyRepository = Injection.provideRepository(this)
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(userRepository)
        )[LoginViewModel::class.java]

        storyViewModel = ViewModelProvider(
            this,
            ViewModelFactory(storyRepository = storyRepository)
        )[StoryViewModel::class.java]

        loginViewModel.getUser().observe(this) {
            userModel = it
        }
        binding.descriptionEditText.error = "Masukkan deskripsi"

        binding.descriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                binding.descriptionEditText.error = when {
                    text.toString().isEmpty() -> {
                        "Masukkan deskripsi"
                    }
                    else -> {
                        null
                    }
                }
            }

            override fun afterTextChanged(text: Editable) {

            }
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.add_story)


        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getMyLastLocation()
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    showStartMarker(it)
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            mMap.isMyLocationEnabled = true

            mMap.setOnMyLocationClickListener {
                showStartMarker(it)
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


    private fun showStartMarker(location: Location) {
        startLocation = LatLng(location.latitude, location.longitude)
        Log.e("TAG", "showStartMarker: ${startLocation!!.latitude} : ${startLocation!!.longitude}")
        mMap.addMarker(
            MarkerOptions().position(startLocation!!).title("My location")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation!!, 17f))
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
                    getMyLastLocation()
                }
                it[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    //Only approximate location access granted
                    getMyLastLocation()
                }
                else -> {


                }
            }

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

    private fun startTakePhoto() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntent.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launcherIntent.launch(chooser)
    }

    private fun uploadImage() {
        try {
            when {
                startLocation == null -> {
                    throw java.lang.Exception("Tidak mendapatkan lokasi anda!")
                }
                getFile == null -> {
                    throw Exception("Silahkan masukkan gambar terlebih dahulu!")
                }

                binding.descriptionEditText.error != null -> {
                    binding.descriptionEditText.requestFocus()
                    throw Exception(binding.descriptionEditText.error.toString())
                }
            }

            val file = reduceFileImage(getFile as File)

            val description =
                binding.descriptionEditText.text.toString()
                    .toRequestBody("text/plain".toMediaType())
            val lat = startLocation!!.latitude
            val lon = startLocation!!.longitude
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part =
                MultipartBody.Part.createFormData("photo", file.name, requestImageFile)

            storyViewModel.uploadStory(
                userModel.token,
                imageMultipart,
                description,
                lat.toFloat(),
                lon.toFloat(),
                this).observe(
                this
            ) {
                onSuccess(it.message)
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT)
                .show()
        }
    }


    private val launcherIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        lateinit var result: Bitmap
        if (it.resultCode == RESULT_OK) {
            val selectedImg = it.data?.data as Uri
            val myFile = uriToFile(selectedImg, application)
            result = BitmapFactory.decodeFile(myFile.path)
            getFile = myFile

        } else if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            if(it.data!=null){
                result = rotateBitmap(
                    BitmapFactory.decodeFile(myFile.path),
                    isBackCamera
                )


                getFile = bitmapToFile(myFile, result)
                binding.previewImageView.setImageBitmap(result)
             }

            }

    }

    override fun showLoading() {
        binding.layoutProgress.placeProgressBar.visibility = View.VISIBLE
    }

    override fun dismissLoading() {
        binding.layoutProgress.placeProgressBar.visibility = View.GONE
    }

    override fun onSuccess(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.success))
            setMessage(message)
            setPositiveButton(getString(R.string.next)) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            create()
            show()
        }
    }

    override fun onFailure(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.failure))
            setMessage(message)
            setPositiveButton(getString(R.string.next)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }


}