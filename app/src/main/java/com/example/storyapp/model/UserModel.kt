package com.example.storyapp.model

data class UserModel(
    val name: String,
    val token: String="",
    var isLogin: Boolean
)