package com.example.storyapp.view

import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.MainActivity
import com.example.storyapp.R
import com.example.storyapp.Utils.Companion.fadeInAnimation
import com.example.storyapp.Utils.Companion.onClick
import com.example.storyapp.Utils.Companion.setUpView
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.preference.UserPreference
import com.example.storyapp.repository.UserRepository
import com.example.storyapp.viewmodel.CallBackResponse
import com.example.storyapp.viewmodel.LoginViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity(), CallBackResponse {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpView(window, supportActionBar)

        userRepository = UserRepository(UserPreference.getInstance(dataStore))

        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(userRepository)
        )[LoginViewModel::class.java]


        playAnimation()
        setupAction()
    }

    private fun setupAction() {

        binding.signupButton onClick {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }

        binding.loginButton onClick {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            try {
                when {
                    binding.emailEditText.error != null -> {
                        binding.emailEditText.requestFocus()
                        throw Exception(binding.emailEditText.error.toString())
                    }

                    binding.passwordEditText.error != null -> {
                        binding.passwordEditText.requestFocus()
                        throw Exception(binding.passwordEditText.error.toString())
                    }
                    else -> {
                        loginViewModel.login(email, password, this).observe(
                            this
                        ) {
                            loginViewModel.saveUser(it.loginResult)
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

        val emailTextView =
            fadeInAnimation(binding.emailTextView)
        val emailEditTextLayout =
            fadeInAnimation(binding.emailEditTextLayout)
        val passwordTextView =
            fadeInAnimation(binding.passwordTextView)
        val passwordEditTextLayout =
            fadeInAnimation(binding.passwordEditTextLayout)
        val login = fadeInAnimation(binding.loginButton)
        val titleChoose = fadeInAnimation(binding.textViewChoose)
        val signUp = fadeInAnimation(binding.signupButton)

        val animatorSet = AnimatorSet().apply {
            playTogether(login, titleChoose, signUp)
        }


        AnimatorSet().apply {
            playSequentially(
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                animatorSet
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
        loginViewModel.login()
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
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