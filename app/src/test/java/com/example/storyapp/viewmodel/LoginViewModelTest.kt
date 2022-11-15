package com.example.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.LiveDataTestUtil.getOrAwaitValue
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.DataDummy
import com.example.storyapp.model.LoginResponse
import com.example.storyapp.repository.UserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatchRules = MainDispatcherRule()


    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var loginViewModel: LoginViewModel
    private val dummyLoginSuccess = DataDummy.generateDummyLoginResponseSuccess()
    private val dummyLoginFailure = DataDummy.generateDummyLoginResponseFailure()

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(userRepository)
    }

    @Test
    fun `when Input Login Return Success`() {
        val expectedLogin = MutableLiveData<LoginResponse>()
        expectedLogin.value = dummyLoginSuccess
        val email = "test@mail.co.id"
        val password = "password"
        `when`(userRepository.postLogin(email,password)).thenReturn(expectedLogin)
        val actualLogin = loginViewModel.login(email,password).getOrAwaitValue()
        Mockito.verify(userRepository).postLogin(email,password)
        assertNotNull(actualLogin)
        assertTrue(actualLogin.loginResult.isLogin)
        assertEquals("Success Login",actualLogin.message)
        assertFalse(actualLogin.error)
    }

    @Test
    fun `when Input Login Return Failure`() {
        val expectedLogin = MutableLiveData<LoginResponse>()
        expectedLogin.value = dummyLoginFailure
        val email = "test@mail.co.id"
        val password = "password123"
        `when`(userRepository.postLogin(email,password)).thenReturn(expectedLogin)
        val actualLogin = loginViewModel.login(email,password).getOrAwaitValue()
        Mockito.verify(userRepository).postLogin(email,password)
        assertNotNull(actualLogin)
        assertTrue(actualLogin.error)
        assertFalse(actualLogin.loginResult.isLogin)
        assertEquals("Login Failure",actualLogin.message)
    }
}