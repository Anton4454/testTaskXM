package com.example.taskxm.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskxm.model.Answer
import com.example.taskxm.model.SurveyState
import com.example.taskxm.repository.SurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val repository: SurveyRepository
) : ViewModel() {

    companion object {
        const val ERROR_MESSAGE_LOAD = "Failed to load questions"
        const val SUBMISSION_SUCCESS = "Success!"
        const val SUBMISSION_FAILURE = "Failure..."
        const val ERROR_MESSAGE_EMPTY_ANSWER = "Answer is empty"
        const val SUBMISSION_FAILED_MESSAGE = "Submission failed"
    }

    private val _state = MutableStateFlow<SurveyState>(SurveyState.Loading)
    val state: StateFlow<SurveyState> = _state

    init {
        fetchQuestions()
    }

    fun fetchQuestions() {
        viewModelScope.launch {
            try {
                val questions = repository.getQuestions()
                _state.value = SurveyState.QuestionsLoaded(questions, 0, mutableMapOf(), setOf(), "")
            } catch (e: Exception) {
                _state.value = SurveyState.Error(ERROR_MESSAGE_LOAD)
            }
        }
    }

    fun onAnswerChange(answer: String, questionId: Int) {
        if (_state.value is SurveyState.QuestionsLoaded) {
            val currentState = _state.value as SurveyState.QuestionsLoaded
            currentState.answers[questionId] = answer
            _state.value = currentState.copy(answers = currentState.answers)
        }
    }

    suspend fun onSubmitAnswer(questionId: Int): Result<Unit> {
        val currentState = _state.value

        if (currentState is SurveyState.QuestionsLoaded) {
            val answer = currentState.answers[questionId]

            if (!answer.isNullOrEmpty()) {
                return try {
                    val result = repository.submitAnswer(Answer(questionId, answer))
                    if (result.isSuccess) {
                        val updatedSubmittedQuestions = currentState.submittedQuestions + questionId
                        _state.value = currentState.copy(
                            submittedQuestions = updatedSubmittedQuestions,
                            submissionMessage = SUBMISSION_SUCCESS
                        )
                    } else {
                        _state.value = currentState.copy(submissionMessage = SUBMISSION_FAILURE)
                    }
                    result
                } catch (e: Exception) {
                    _state.value = currentState.copy(submissionMessage = SUBMISSION_FAILURE)
                    Result.failure(e)
                }
            }
        }
        return Result.failure(Exception(ERROR_MESSAGE_EMPTY_ANSWER))
    }
}