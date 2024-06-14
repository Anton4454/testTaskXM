package com.example.taskxm

import com.example.taskxm.model.Question
import com.example.taskxm.model.SurveyState
import com.example.taskxm.repository.SurveyRepository
import com.example.taskxm.ui.SurveyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SurveyViewModelTest {

    private lateinit var viewModel: SurveyViewModel
    private lateinit var mockRepository: SurveyRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock()
        viewModel = SurveyViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchQuestions updates state to QuestionsLoaded on success`() = runTest {
        val questions = listOf(Question(1, "Question 1"))
        whenever(mockRepository.getQuestions()).thenReturn(questions)

        viewModel.fetchQuestions()

        val state = viewModel.state.first()
        assertTrue(state is SurveyState.QuestionsLoaded)
        assertEquals(questions, (state as SurveyState.QuestionsLoaded).questions)
    }
}