package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.model.RegisterResponse
import com.example.storyapp.repository.UserRepository

class SignUpViewModel(private val userRepository: UserRepository?) : ViewModel() {

    fun register(
        name: String,
        email: String,
        password: String,
        callBackResponse: CallBackResponse?=null
    ): LiveData<RegisterResponse> {
        return userRepository!!.postRegister(name, email, password, callBackResponse)
    }
}