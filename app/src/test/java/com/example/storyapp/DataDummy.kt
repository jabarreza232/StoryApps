package com.example.storyapp

import com.example.storyapp.model.*

object DataDummy {
    fun generateDummyLoginResponseSuccess(): LoginResponse {
        return LoginResponse(false, "Success Login", UserModel("test", "", true))
    }

    fun generateDummyLoginResponseFailure(): LoginResponse {
        return LoginResponse(true, "Login Failure", UserModel("", "", false))
    }

    fun generateDummyRegisterResponseSuccess(): RegisterResponse {
        return RegisterResponse(false, "Success Register")
    }

    fun generateDummyRegisterResponseFailure(): RegisterResponse {
        return RegisterResponse(true, "Register Failure")
    }

    fun generateDummyUploadStoryResponseSuccess(): UploadStoryResponse {
        return UploadStoryResponse(false, "Success Upload Story")
    }

    fun generateDummyUploadStoryResponseFailure(): UploadStoryResponse {
        return UploadStoryResponse(true, "Failure Upload Story")
    }


    fun generateDummyStoryResponse(): List<StoryModel> {
        val items: MutableList<StoryModel> = arrayListOf()
        for (i in 0..50) {
            val story = StoryModel(
                i.toString(),
                "name $i",
                "description $i",
                "photoUrl $i",
                "createdAt $i",
                i.toDouble(),
                i.toDouble(),
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyStoryNull(): List<StoryModel> {
        val items: MutableList<StoryModel> = arrayListOf()

        return items
    }
}