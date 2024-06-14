package com.example.taskxm.model

sealed class SurveyState {
    object Loading : SurveyState()
    data class QuestionsLoaded(
        val questions: List<Question>,
        val currentIndex: Int,
        val answers: MutableMap<Int, String>,
        val submittedQuestions: Set<Int>,
        val submissionMessage: String
    ) : SurveyState()
    data class Error(val message: String) : SurveyState()
}