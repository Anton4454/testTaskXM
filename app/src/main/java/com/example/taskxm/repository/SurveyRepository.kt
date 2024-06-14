package com.example.taskxm.repository

import com.example.taskxm.model.Answer
import com.example.taskxm.model.Question

interface SurveyRepository {
    suspend fun getQuestions(): List<Question>
    suspend fun submitAnswer(answer: Answer): Result<Unit>
}