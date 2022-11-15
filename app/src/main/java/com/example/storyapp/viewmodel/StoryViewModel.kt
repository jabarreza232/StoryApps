package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.model.RegisterResponse
import com.example.storyapp.model.StoryModel
import com.example.storyapp.model.UploadStoryResponse
import com.example.storyapp.preference.StoryPreference
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepository: StoryRepository?) : ViewModel() {

    fun getAllStories():LiveData<List<StoryModel>> = storyRepository!!.getStories()

    fun getAllStoriesWithPage(
        token: String,
        location:Int
    ): LiveData<PagingData<StoryModel>> {
        return storyRepository!!.getStoriesWithPage(token,location).cachedIn(viewModelScope)
    }

    fun uploadStory(
        token:String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        latitude:Float,
        longitude:Float,
        callBackResponse: CallBackResponse?=null
    ) : LiveData<UploadStoryResponse> {
     return   storyRepository!!.uploadStory(token,imageMultipart, description, latitude,longitude, callBackResponse)
    }
}