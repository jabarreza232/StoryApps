package com.example.storyapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.Utils.Companion.onClick
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.model.StoryModel
import com.example.storyapp.preference.StoryPreference
import com.example.storyapp.preference.UserPreference
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.repository.UserRepository
import com.example.storyapp.view.AddStoryActivity
import com.example.storyapp.view.LoginActivity
import com.example.storyapp.view.MapStoryActivity
import com.example.storyapp.viewmodel.CallBackResponse
import com.example.storyapp.viewmodel.LoginViewModel
import com.example.storyapp.viewmodel.StoryViewModel


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity(), CallBackResponse {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var storyRepository: StoryRepository
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyPreference: StoryPreference
    private  var listStory:List<StoryModel> = mutableListOf()
    companion object {


        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
              REQUIRED_PERMISSIONS
                ,

                REQUEST_CODE_PERMISSIONS
            )
        }

        userRepository = UserRepository(UserPreference.getInstance(dataStore))

        storyRepository =Injection.provideRepository(this)
        storyPreference = StoryPreference.getInstance(dataStore)

        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(userRepository)
        )[LoginViewModel::class.java]

        storyViewModel = ViewModelProvider(
            this,
            ViewModelFactory(storyRepository = storyRepository)
        )[StoryViewModel::class.java]
        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvStories.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.rvStories.layoutManager = LinearLayoutManager(this)
        }
        val listStoryAdapter = ListStoryAdapter()

        binding.rvStories.adapter = listStoryAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                listStoryAdapter.retry()
            }
        )
        loginViewModel.getUser().observe(this) { userModel ->
            if (userModel != null) {
                if (userModel.isLogin) {
                    Log.e("TAG", "onCreate: ${userModel.token}")
                    storyViewModel.getAllStoriesWithPage(userModel.token,1).observe(this) { pagingData ->

                        showRecyclerList(pagingData,listStoryAdapter)
                    }
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }

        }

        storyViewModel.getAllStories().observe(this){
            listStory= it
        }



        binding.fabAddStory onClick {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
        binding.fabLocation onClick {
            val intent =Intent(this,MapStoryActivity::class.java)
            val array = ArrayList<StoryModel>()
            for (story in listStory)
                array.add(story)

            intent.putParcelableArrayListExtra("stories",array)
            startActivity(intent)
        }
    }


    private fun showRecyclerList(listStory: PagingData<StoryModel>,listStoryAdapter:ListStoryAdapter) {
        listStoryAdapter.submitData(lifecycle,listStory)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                loginViewModel.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}