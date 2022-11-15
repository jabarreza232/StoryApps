package com.example.storyapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.model.LoginResponse
import com.example.storyapp.model.RegisterResponse
import com.example.storyapp.network.ApiConfig
import com.example.storyapp.network.ApiConfig.getApiService
import com.example.storyapp.preference.UserPreference
import com.example.storyapp.viewmodel.CallBackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(val pref: UserPreference) {


    fun postLogin(
        email: String,
        password: String,
        callBackResponse: CallBackResponse?=null
    ): LiveData<LoginResponse> {
        val loginResponseLiveData: MutableLiveData<LoginResponse> = MutableLiveData()

        callBackResponse!!.showLoading()
        val service = getApiService().login(email, password)
        service.enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                callBackResponse.dismissLoading()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        loginResponseLiveData.value = responseBody
                    }
                } else {
                    callBackResponse.onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callBackResponse.dismissLoading()
                callBackResponse.onFailure(t.message.toString())
            }

        })
        return loginResponseLiveData
    }

    fun postRegister(
        name: String,
        email: String,
        password: String,
        callBackResponse: CallBackResponse?=null
    ): LiveData<RegisterResponse> {
        val registerResponseLiveData: MutableLiveData<RegisterResponse> = MutableLiveData()

        val service = getApiService().register(name, email, password)
        callBackResponse!!.showLoading()
        service.enqueue(object :
            Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                callBackResponse.dismissLoading()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        registerResponseLiveData.value = responseBody
                    }
                } else {
                    callBackResponse.onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                callBackResponse.dismissLoading()
                callBackResponse.onFailure(t.message.toString())
            }

        })
        return registerResponseLiveData
    }


}