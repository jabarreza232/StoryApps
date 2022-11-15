package com.example.storyapp.view

import android.animation.AnimatorSet
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.Utils.Companion.fadeInAnimation
import com.example.storyapp.Utils.Companion.onClick
import com.example.storyapp.Utils.Companion.setUpView
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.ActivitySignUpBinding
import com.example.storyapp.preference.UserPreference
import com.example.storyapp.repository.UserRepository
import com.example.storyapp.viewmodel.CallBackResponse
import com.example.storyapp.viewmodel.SignUpViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignUpActivity : AppCompatActivity(), CallBackResponse {
    private lateinit var binding: ActivitySignUpBinding

    private lateinit var signupViewModel: SignUpViewModel
    private lateinit var userRepository: UserRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpView(window, supportActionBar)
        userRepository = UserRepository(UserPreference.getInstance(dataStore))

        signupViewModel = ViewModelProvider(
            this,
            ViewModelFactory(userRepository)
        )[SignUpViewModel::class.java]


        playAnimation()
        setupAction()
    }

    private fun setupAction() {
        binding.nameEditText.error = "Masukkan nama"
        binding.imgClose onClick {
            finish()
        }
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                binding.nameEditText.error = when {
                    text.toString().isEmpty() -> {
                        "Masukkan nama"
                    }
                    else -> {
                        null
                    }
                }
            }

            override fun afterTextChanged(text: Editable) {

            }
        })


        binding.signupButton onClick {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            try {
                when {
                    binding.nameEditText.error != null -> {
                        binding.nameEditText.requestFocus()
                        throw Exception(binding.nameEditText.error.toString())
                    }
                    binding.emailEditText.error != null -> {
                        binding.emailEditText.requestFocus()
                        throw Exception(binding.emailEditText.error.toString())
                    }

                    binding.passwordEditText.error != null -> {
                        binding.passwordEditText.requestFocus()
                        throw Exception(binding.passwordEditText.error.toString())
                    }
                    else -> {
                        signupViewModel.register(name, email, password, this).observe(
                            this
                        ) {
                            onSuccess(it.message)
                        }
                    }
                }
            } catch (exception: Exception) {
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun playAnimation() {


        val nameTextView = fadeInAnimation(binding.nameTextView)
        val nameEditTextLayout = fadeInAnimation(binding.nameEditTextLayout)
        val emailTextView = fadeInAnimation(binding.emailTextView)
        val emailEditTextLayout = fadeInAnimation(binding.emailEditTextLayout)
        val passwordTextView = fadeInAnimation(binding.passwordTextView)
        val passwordEditTextLayout = fadeInAnimation(binding.passwordEditTextLayout)
        val signup = fadeInAnimation(binding.signupButton)


        AnimatorSet().apply {
            playSequentially(
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 500
        }.start()
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