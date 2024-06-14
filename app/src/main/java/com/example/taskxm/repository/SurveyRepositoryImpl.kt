package com.example.taskxm.repository

import com.example.taskxm.model.Answer
import com.example.taskxm.model.Question
import com.example.taskxm.network.ApiService
import com.example.taskxm.ui.SurveyViewModel.Companion.SUBMISSION_FAILED_MESSAGE
import javax.inject.Inject

class SurveyRepositoryImpl @Inject constructor(private val api: ApiService) : SurveyRepository {

    override suspend fun getQuestions(): List<Question> = api.getQuestions()

    override suspend fun submitAnswer(answer: Answer): Result<Unit> {
        return try {
            val response = api.submitAnswer(answer)
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception(SUBMISSION_FAILED_MESSAGE))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}