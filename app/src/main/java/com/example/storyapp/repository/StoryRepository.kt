package com.example.storyapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.example.storyapp.Utils.Companion.BEARER
import com.example.storyapp.database.StoryDatabase
import com.example.storyapp.model.RegisterResponse
import com.example.storyapp.model.StoryModel
import com.example.storyapp.model.UploadStoryResponse
import com.example.storyapp.network.ApiService
import com.example.storyapp.viewmodel.CallBackResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService? = null
) {


    fun getStoriesWithPage(
        token: String,
        location: Int

    ): LiveData<PagingData<StoryModel>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),

            remoteMediator = StoryRemoteMediator(token, location, storyDatabase, apiService!!),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStoryWithPage()
            }
        ).liveData
    }

     fun getStories(): LiveData<List<StoryModel>> = storyDatabase.storyDao().getAllStory()

     fun getStoriesWidget(): List<StoryModel> = storyDatabase.storyDao().getAllStoryWidget()



    fun uploadStory(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        latitude: Float,
        longitude: Float,
        callBackResponse: CallBackResponse?=null
    ): LiveData<UploadStoryResponse>  {
        val uploadStoryResponseLiveData: MutableLiveData<UploadStoryResponse> = MutableLiveData()
        callBackResponse!!.showLoading()
        apiService!!.uploadStory(BEARER + token, imageMultipart, description, latitude, longitude)
            .enqueue(object :
                Callback<UploadStoryResponse> {
                override fun onResponse(
                    call: Call<UploadStoryResponse>,
                    response: Response<UploadStoryResponse>
                ) {
                    callBackResponse.dismissLoading()

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            uploadStoryResponseLiveData.value  = responseBody
                        }
                    } else {
                        callBackResponse.onFailure(response.message())
                    }
                }

                override fun onFailure(call: Call<UploadStoryResponse>, t: Throwable) {
                    callBackResponse.dismissLoading()
                    callBackResponse.onFailure(t.message.toString())
                }
            })

        return  uploadStoryResponseLiveData
    }
}