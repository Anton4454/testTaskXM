package com.example.taskxm.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskxm.model.SurveyState
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.taskxm.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsScreen(viewModel: SurveyViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsState()
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    var notificationMessage by remember { mutableStateOf<String?>(null) }
    val successText = stringResource(id = R.string.success)
    val failureText = stringResource(id = R.string.failure)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.survey_title)) })
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (val uiState = state) {
                    is SurveyState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is SurveyState.QuestionsLoaded -> {
                        val questions = uiState.questions
                        val currentIndex = pagerState.currentPage
                        val currentQuestion = questions[currentIndex]
                        var answer by remember { mutableStateOf(uiState.answers[currentQuestion.id] ?: "") }

                        Text(stringResource(id = R.string.submitted_questions_count, uiState.submittedQuestions.size, questions.size))
                        HorizontalPager(
                            count = questions.size,
                            state = pagerState
                        ) { page ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(stringResource(id = R.string.question_number, page + 1, questions.size))
                                Text(questions[page].question)
                                BasicTextField(
                                    value = uiState.answers[questions[page].id] ?: "",
                                    onValueChange = {
                                        answer = it
                                        viewModel.onAnswerChange(it, questions[page].id)
                                    },
                                    modifier = Modifier
                                        .height(dimensionResource(id = R.dimen.basic_text_field_height))
                                        .padding(dimensionResource(id = R.dimen.padding_normal))
                                )
                            }
                        }
                        Row {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(currentIndex - 1)
                                    }
                                },
                                enabled = currentIndex > 0
                            ) {
                                Text(stringResource(id = R.string.previous))
                            }
                            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_normal)))
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(currentIndex + 1)
                                    }
                                },
                                enabled = currentIndex < questions.size - 1
                            ) {
                                Text(stringResource(id = R.string.next))
                            }
                        }
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_normal)))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val result = viewModel.onSubmitAnswer(currentQuestion.id)
                                    notificationMessage = if (result.isSuccess) {
                                        successText
                                    } else {
                                        failureText
                                    }
                                    delay(timeMillis = 3_000)
                                    notificationMessage = null
                                }
                            },
                            enabled = answer.isNotEmpty() && !uiState.submittedQuestions.contains(currentQuestion.id)
                        ) {
                            Text(
                                if (uiState.submittedQuestions.contains(currentQuestion.id)) stringResource(id = R.string.already_submitted)
                                else stringResource(id = R.string.submit)
                            )
                        }
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_normal)))

                        notificationMessage?.let { message ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(id = R.dimen.padding_normal)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(message)
                                    if (message == stringResource(id = R.string.failure)) {
                                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_normal)))
                                        Button(onClick = {
                                            coroutineScope.launch {
                                                viewModel.onSubmitAnswer(currentQuestion.id)
                                            }
                                        }) {
                                            Text(stringResource(id = R.string.retry))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is SurveyState.Error -> {
                        Text(uiState.message)
                    }
                }
            }
        }
    )
}
