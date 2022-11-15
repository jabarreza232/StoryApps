package com.example.storyapp.network

import com.example.storyapp.model.LoginResponse
import com.example.storyapp.model.RegisterResponse
import com.example.storyapp.model.StoryResponse
import com.example.storyapp.model.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<RegisterResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: Float,
        @Part("lon") longitude: Float,
    ): Call<UploadStoryResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<RegisterResponse>


    @GET("stories")
    fun getStories(@Header("Authorization") token: String,@Query("location") location:Int): Call<StoryResponse>

    @GET("stories")
    suspend fun getStoriesWithPage(@Header("Authorization") token: String, @Query("page")page:Int,@Query("size")size:Int, @Query("location") location:Int): StoryResponse
}