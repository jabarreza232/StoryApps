package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.database.StoryDatabase
import com.example.storyapp.network.ApiConfig
import com.example.storyapp.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}