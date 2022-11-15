package com.example.storyapp.model

import com.google.gson.annotations.SerializedName

data class UploadStoryResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String,
)