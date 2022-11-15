package com.example.storyapp.model

data class LoginResponse(val error: Boolean, val message: String, val loginResult: UserModel)