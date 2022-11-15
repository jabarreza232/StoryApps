package com.example.storyapp.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.model.StoryModel
@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoryModel>)

    @Query("SELECT * FROM story")
    fun getAllStoryWithPage(): PagingSource<Int, StoryModel>
    @Query("SELECT * FROM story")
    fun getAllStory(): LiveData<List<StoryModel>>
    @Query("SELECT * FROM story LIMIT 10")
     fun getAllStoryWidget(): List<StoryModel>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}