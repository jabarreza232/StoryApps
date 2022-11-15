package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.model.LoginResponse
import com.example.storyapp.model.UserModel
import com.example.storyapp.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository?) : ViewModel() {

    fun getUser(): LiveData<UserModel> {
        return userRepository!!.pref.getUser().asLiveData()
    }

    fun login(
        email: String,
        password: String,
        callBackResponse: CallBackResponse?=null
    ): LiveData<LoginResponse> {
        return userRepository!!.postLogin(email, password, callBackResponse)
    }

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            userRepository!!.pref.saveUser(user)
        }
    }

    fun login() {
        viewModelScope.launch {
            userRepository!!.pref.login()
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository!!.pref.logout()
        }
    }
}