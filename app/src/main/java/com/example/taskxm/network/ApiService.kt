package com.example.taskxm.network

import com.example.taskxm.model.Answer
import com.example.taskxm.model.Question
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/questions")
    suspend fun getQuestions(): List<Question>

    @POST("/question/submit")
    suspend fun submitAnswer(@Body answer: Answer): Response<Unit>
}