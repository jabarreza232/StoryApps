package com.example.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.DataDummy
import com.example.storyapp.LiveDataTestUtil.getOrAwaitValue
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.model.StoryModel
import com.example.storyapp.model.UploadStoryResponse
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatchRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var storyViewModel: StoryViewModel
    private val dummyUploadStoryFailure = DataDummy.generateDummyUploadStoryResponseFailure()
    private val dummyUploadStorySuccess = DataDummy.generateDummyUploadStoryResponseSuccess()
    val token ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXU2aHlfdG5hckFKalBUc3giLCJpYXQiOjE2Njc5MTM1OTd9.-nS6O-V81kN3PWoVg1oJpb1-Hu6W0nNtDV_zT2kTEpE"

    @Before
    fun setUp() {
         storyViewModel = StoryViewModel(storyRepository)
    }
    @Test
    fun `when Get Story With Page Should Not Null`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<StoryModel> = QuotePagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoryModel>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getStoriesWithPage(token,1)).thenReturn(expectedStory)

        val actualQuote: PagingData<StoryModel> = storyViewModel.getAllStoriesWithPage(token,1).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualQuote)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
    }

    @Test
    fun `when Get Story Should Not Null`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: List<StoryModel> = dummyStory
        val expectedStory = MutableLiveData<List<StoryModel>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStory)

        val actualQuote: List<StoryModel> = storyViewModel.getAllStories().getOrAwaitValue()
        Mockito.verify(storyRepository).getStories()

        assertNotNull(actualQuote)
        assertEquals(dummyStory, actualQuote)
        assertEquals(dummyStory.size, actualQuote.size)
        assertEquals(dummyStory[0].name, actualQuote[0]?.name)
    }

    @Test
    fun `when Get Story Should Empty Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryNull()
        val data: List<StoryModel> = dummyStory
        val expectedStory = MutableLiveData<List<StoryModel>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStory)

        val actualQuote: List<StoryModel> = storyViewModel.getAllStories().getOrAwaitValue()
        Mockito.verify(storyRepository).getStories()


        assertEquals(0, actualQuote.size)
        assertEquals(dummyStory.size, actualQuote.size)
    }

    @Test
    fun `when Get Story With Page Should Empty Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryNull()
        val data: PagingData<StoryModel> = QuotePagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoryModel>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getStoriesWithPage(token,1)).thenReturn(expectedStory)

        val storyViewModel = StoryViewModel(storyRepository)
        val actualQuote: PagingData<StoryModel> = storyViewModel.getAllStoriesWithPage(token,1).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualQuote)

        assertEquals(0,differ.snapshot().size)
    }


    @Test
    fun `when Input Upload Story Return Success`() {
        val expectedUploadStory = MutableLiveData<UploadStoryResponse>()
        expectedUploadStory.value = dummyUploadStorySuccess
        val description = "Kenangan kenangan di masa lampau".toRequestBody("text/plain".toMediaType())
        val lat = -6.3222025f
        val lon = 106.85816f
        val file= File("0/photos/story.jpg")
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", "story.jpg", requestImageFile)

        Mockito.`when`(storyRepository.uploadStory(token,imageMultipart,description, lat,lon)).thenReturn(expectedUploadStory)
        val actualUploadStory = storyViewModel.uploadStory(token,imageMultipart,description, lat,lon).getOrAwaitValue()
        Mockito.verify(storyRepository).uploadStory(token,imageMultipart,description, lat,lon)
        assertNotNull(actualUploadStory)
        assertEquals("Success Upload Story",actualUploadStory.message)
        assertFalse(actualUploadStory.error)
    }

    @Test
    fun `when Input Upload Story Return Failure`() {
        val expectedUploadStory = MutableLiveData<UploadStoryResponse>()
        expectedUploadStory.value = dummyUploadStoryFailure
        val description = "Kenangan kenangan di masa lampau".toRequestBody("text/plain".toMediaType())
        val lat = 0.0f
        val lon = 0.0f
        val file= File("0/photos/story.xlsx")
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", "story.xlsx", requestImageFile)

        Mockito.`when`(storyRepository.uploadStory(token,imageMultipart,description, lat,lon)).thenReturn(expectedUploadStory)
        val actualUploadStory = storyViewModel.uploadStory(token,imageMultipart,description, lat,lon).getOrAwaitValue()
        Mockito.verify(storyRepository).uploadStory(token,imageMultipart,description, lat,lon)
        assertNotNull(actualUploadStory)
        assertEquals("Failure Upload Story",actualUploadStory.message)
        assertTrue(actualUploadStory.error)
    }


    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {

        }

        override fun onRemoved(position: Int, count: Int) {
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
        }

    }

    class QuotePagingSource : PagingSource<Int, LiveData<List<StoryModel>>>() {
        companion object {
            fun snapshot(items: List<StoryModel>): PagingData<StoryModel> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryModel>>>): Int? {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryModel>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }

    }
}