package okik.tech.jetcert

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource

import jetcert.shared.generated.resources.Res
import jetcert.shared.generated.resources.app_name
import jetcert.shared.generated.resources.compose_multiplatform
import jetcert.shared.generated.resources.plural_news
import jetcert.shared.generated.resources.string_template
import kotlinx.coroutines.launch
import okik.tech.jetcert.db.News
import okik.tech.jetcert.db.TopRepo
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

object Doggy {
    const val hola = "hola"
}


@Composable
@Preview
fun App() {
    val viewModel: MainViewModel = koinViewModel<MainViewModel>()

    MaterialTheme {
        val coroutineScope = rememberCoroutineScope()
        val state = viewModel.uiState.collectAsStateWithLifecycle()
//        var showContent by remember { mutableStateOf(false) }


        Row(
            Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            Left(
                onGetNews = {
                    coroutineScope.launch {
                        //                    viewModel.getStories()
                        viewModel.insertNewsToDB()
                    }
                },
                onGetRepos = {
                    coroutineScope.launch {
                        //                    viewModel.getTopRepos()
                        viewModel.insertTopRepos()
                    }
                },
                onInsertNews = { news ->
                    coroutineScope.launch {
                        viewModel.insertFakeNews(news)
                    }
                },
                shouldShow = !state.value.stories.isNullOrEmpty() || !state.value.topRepos.isNullOrEmpty(),
                news = state.value.stories,
                topRepos = state.value.topRepos
            )

            Right(
                viewModel.uiState.value.hasShownOnboarding,
                viewModel.uiState.value.token,
                viewModel.uiState.value.stars,
                viewModel.uiState.value.counter,
                viewModel.uiState.value.reactiveCounterOne,
                viewModel.uiState.value.person,
                { viewModel.toggleHashShownOnboarding() },

                { token -> viewModel.updateToken(token) },
                { viewModel.addStar() },
                { viewModel.subtractStar() },
                { viewModel.incrementCounter() },
                { viewModel.decrementCounter() },
                { viewModel.incrementReactiveCounter() },
                { viewModel.decrementReactiveCounter() },
                { person -> viewModel.savePerson(person) }
            )
        }
    }
}

@Composable
fun RowScope.Right(
    hasShownOnboarding: Boolean,
    token: String,
    stars: UByte,
    counter: Byte,
    reactiveCounterOne: UByte,
    person: Person?,
    onHasShownOnboardingToggle: () -> Unit,
    onTokenChange: (String) -> Unit,
    onStarIncrement: () -> Unit,
    onStarDecrement: () -> Unit,
    onCounterIncrement: () -> Unit,
    onCounterDecrement: () -> Unit,
    onReactiveCounterOneIncrement: () -> Unit,
    onReactiveCounterOneDecrement: () -> Unit,
    onPersonChange: (String) -> Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f, true)
            .verticalScroll(rememberScrollState())
            .padding(start = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Stored HasShownOnBoarding $hasShownOnboarding", textAlign = TextAlign.Center)
        Button(onClick = onHasShownOnboardingToggle) {
            Text("Toggle HasShownOnBoarding", textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(16.dp))
        var tokenInput = rememberTextFieldState("")
        Text("Stored Token $token", textAlign = TextAlign.Center)
        TextField(
            onValueChange = { input ->
                tokenInput.setTextAndPlaceCursorAtEnd(input)
                onTokenChange(input)
            },
            value = tokenInput.text.toString(),
            placeholder = {
                Text("Token to save", textAlign = TextAlign.Center)
            }
        )

        Spacer(Modifier.height(16.dp))
        Text("Stored Stars $stars", textAlign = TextAlign.Center)
        Button(onClick = onStarDecrement) {
            Text("Star--")
        }
        Button(onClick = onStarIncrement) {
            Text("Star++")
        }

        Spacer(Modifier.height(16.dp))
        Text("Stored Counter $counter")
        Button(onClick = onCounterDecrement) {
            Text("Count--")
        }
        Button(onClick = onCounterIncrement) {
            Text("Count++")
        }

        Spacer(Modifier.height(16.dp))
        Text("Stored Reactive Count $reactiveCounterOne", textAlign = TextAlign.Center)
        Button(onClick = onReactiveCounterOneDecrement) {
            Text("ReactCount--", textAlign = TextAlign.Center)
        }
        Button(onClick = onReactiveCounterOneIncrement) {
            Text("ReactCount++", textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(16.dp))
        Text("Stored Person $person", textAlign = TextAlign.Center)
        val personInput = rememberTextFieldState()
        val isError = rememberSaveable { mutableStateOf(false) }
        TextField(
            state = personInput,
            isError = isError.value,
            placeholder = {
                Text("Person properties(name, age), use comma")
            }
        )
        Button(onClick = {
            isError.value = !onPersonChange(personInput.text.toString())
        }) {
            Text("Save Person")
        }
    }
}

@Composable
fun RowScope.Left(
    onGetNews: () -> Unit,
    onGetRepos: () -> Unit,
    onInsertNews: (String) -> Unit,
    shouldShow: Boolean,
    news: List<News>?,
    topRepos: List<TopRepo>?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f, true)
            .padding(end = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            DemoLocaleStrings()

            Spacer(Modifier.height(24.dp))

            DbAndApiInteractions(onGetNews, onGetRepos, onInsertNews)
        }

        if (shouldShow) {
            item {
                AnimatedVisibility(
                    visible = shouldShow,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Image(
                            painterResource(Res.drawable.compose_multiplatform),
                            null,
                            modifier = Modifier
                                .size(80.dp, 80.dp)
                        )
                    }
                }
            }

            item(key = 1) {
                AnimatedVisibility(
                    visible = shouldShow,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = "Top 10",
                        fontSize = 30.sp,
                        modifier = Modifier
                    )
                }
            }

            item(key = 2) {
                AnimatedVisibility(
                    visible = shouldShow,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    HorizontalDivider()
                }
            }

            this@LazyColumn.item(key = 3) {
                Spacer(Modifier.height(16.dp))
            }

            if (!news.isNullOrEmpty()) {

                items(
                    items = news,
                    key = { currentNews -> currentNews.id }
                ) { story ->
                    AnimatedVisibility(
                        visible = shouldShow,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Text(
                                text = story.title.orEmpty(),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            } else if (!topRepos.isNullOrEmpty()) {
                this@LazyColumn.items(
                    items = topRepos,
                    key = { currentRepo -> currentRepo.id }
                ) { repo ->
                    AnimatedVisibility(
                        visible = shouldShow,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Text(
                                text = repo.name,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DbAndApiInteractions(
    onGetNews: () -> Unit,
    onGetRepos: () -> Unit,
    onInsertNews: (String) -> Unit
) {
    Button(onClick = onGetNews) {
        Text("Get News(Rest Client)", fontFamily = giffy(), textAlign = TextAlign.Center)
    }

    Spacer(Modifier.height(8.dp))

    Button(onClick = onGetRepos) {
        Text("Get Top Repos(Apollo GraphQl Client)", textAlign = TextAlign.Center)
    }

    Spacer(Modifier.height(8.dp))

    val input = rememberTextFieldState("")
    TextField(input, lineLimits = TextFieldLineLimits.MultiLine(1, 1))

    Spacer(Modifier.height(8.dp))

    Button(onClick = { onInsertNews(input.text.toString()) }) {
        Text("Insert Fake News")
    }
}

@Composable
fun DemoLocaleStrings() {
    Text(text = "String: ${stringResource(resource = Res.string.app_name)}")
    Spacer(Modifier.height(4.dp))

    Text(text = "Plural == 1: ${pluralStringResource(resource = Res.plurals.plural_news, 1)}")
    Spacer(Modifier.height(4.dp))

    Text(text = "Plural > 1: ${pluralStringResource(resource = Res.plurals.plural_news, 2, 2)}")
    Spacer(Modifier.height(4.dp))

    Text(text = "Template: ${stringResource(resource = Res.string.string_template, "Juan", 2)}")
    Spacer(Modifier.height(4.dp))

//    Text(text = "Array[0]: ${stringArrayResource(resource = Res.array.top_news)[0]}")
//    Spacer(Modifier.height(4.dp))
//
//    Text(text = "Array[0]: ${stringArrayResource(resource = Res.array.top_news)[1]}")
//    Spacer(Modifier.height(4.dp))
}