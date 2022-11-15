package com.example.storyapp.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storyapp.Utils.Companion.BEARER
import com.example.storyapp.database.RemoteKeys
import com.example.storyapp.database.StoryDatabase
import com.example.storyapp.model.StoryModel
import com.example.storyapp.network.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(private val token:String, private val location:Int, private val database: StoryDatabase, private val apiService: ApiService) :
    RemoteMediator<Int, StoryModel>() {
    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryModel>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosesToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {

                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                val nextKey =
                    remoteKey?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                nextKey
            }
        }

        return try {
            val responseData = apiService.getStoriesWithPage(BEARER + token,page, state.config.pageSize,location)

           responseData.let {
               val endOfPaginationReached = it.listStory.isEmpty()

               database.withTransaction {
                   if (loadType == LoadType.REFRESH) {
                       database.remoteKeysDao().deleteRemoteKeys()
                       database.storyDao().deleteAll()
                   }

                   val prevKey = if (page == 1) null else page - 1
                   val nextKey = if (endOfPaginationReached) null else page + 1
                   val keys = it.listStory.map {
                       RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                   }
                   database.remoteKeysDao().insertAll(keys)
                   database.storyDao().insertStory(it.listStory)
               }

               MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
           }

        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryModel>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryModel>): RemoteKeys? {
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { data ->
                database.remoteKeysDao().getRemoteKeysId(data.id)
            }
    }


    private suspend fun getRemoteKeyClosesToCurrentPosition(state: PagingState<Int, StoryModel>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

}