package com.example.storyapp.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.storyapp.model.StoryModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class StoryPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getAllStories(): Flow<MutableList<StoryModel>> {

        return dataStore.data.map { preferences ->
            val storyJson = preferences[STORY_KEY] ?: ""
            val type = object : TypeToken<MutableList<StoryModel>>() {}.type
            val stories: MutableList<StoryModel> = Gson().fromJson(storyJson, type)
            stories
        }
    }

    suspend fun saveStory(story: String) {
        dataStore.edit { preferences ->
            preferences[STORY_KEY] = story
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: StoryPreference? = null

        private val STORY_KEY = stringPreferencesKey("story")


        fun getInstance(dataStore: DataStore<Preferences>): StoryPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}