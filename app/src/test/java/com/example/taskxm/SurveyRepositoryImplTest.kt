package com.example.taskxm

import com.example.taskxm.model.Question
import com.example.taskxm.network.ApiService
import com.example.taskxm.repository.SurveyRepository
import com.example.taskxm.repository.SurveyRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class SurveyRepositoryImplTest {

    private lateinit var surveyRepository: SurveyRepository
    private lateinit var mockApiService: ApiService

    @Before
    fun setup() {
        mockApiService = mock()
        surveyRepository = SurveyRepositoryImpl(mockApiService)
    }

    @Test
    fun `getQuestions returns questions from api`(): Unit = runBlocking {
        val expectedQuestions = listOf(
            Question(1, "Question 1"),
            Question(2, "Question 2")
        )
        whenever(mockApiService.getQuestions()).thenReturn(expectedQuestions)

        val actualQuestions = surveyRepository.getQuestions()

        assertEquals(expectedQuestions, actualQuestions)
    }
}