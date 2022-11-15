package com.example.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.DataDummy
import com.example.storyapp.LiveDataTestUtil.getOrAwaitValue
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.model.LoginResponse
import com.example.storyapp.model.RegisterResponse
import com.example.storyapp.repository.UserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignUpViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatchRules = MainDispatcherRule()


    @Mock
    private lateinit var registerRepository: UserRepository
    private lateinit var registerViewModel: SignUpViewModel
    private val dummyRegisterSuccess = DataDummy.generateDummyRegisterResponseSuccess()
    private val dummyRegisterFailure = DataDummy.generateDummyRegisterResponseFailure()

    @Before
    fun setUp() {
        registerViewModel = SignUpViewModel(registerRepository)
    }

    @Test
    fun `when Input Register Return Success`() {
        val expectedRegister = MutableLiveData<RegisterResponse>()
        expectedRegister.value = dummyRegisterSuccess
        val name = "Test123"
        val email = "test@mail.co.id"
        val password = "password"

        Mockito.`when`(registerRepository.postRegister(name,email, password)).thenReturn(expectedRegister)
        val actualRegister = registerViewModel.register(name,email,password).getOrAwaitValue()
        Mockito.verify(registerRepository).postRegister(name,email,password)
        assertNotNull(actualRegister)
        assertEquals("Success Register",actualRegister.message)
        assertFalse(actualRegister.error)
    }

    @Test
    fun `when Input Register Return Failure`() {
        val expectedRegister = MutableLiveData<RegisterResponse>()
        expectedRegister.value = dummyRegisterFailure
        val name = "Test123"
        val email = "test@mail"
        val password = ""
        Mockito.`when`(registerRepository.postRegister(name,email, password)).thenReturn(expectedRegister)
        val actualRegister = registerViewModel.register(name,email,password).getOrAwaitValue()
        Mockito.verify(registerRepository).postRegister(name,email,password)

        assertNotNull(actualRegister)
        assertTrue(actualRegister.error)
        assertEquals("Register Failure",actualRegister.message)
    }
}