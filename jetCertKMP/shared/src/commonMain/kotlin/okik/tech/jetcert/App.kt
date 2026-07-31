package okik.tech.jetcert

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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


        Row(Modifier.background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()) {
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
                state.value.stories,
                state.value.topRepos
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
            .weight(1f, true),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Stored HasShownOnBoarding $hasShownOnboarding")
        Button(onClick = onHasShownOnboardingToggle) {
            Text("Toggle HasShownOnBoarding")
        }

        var tokenInput = rememberTextFieldState("")
        Text("token Stored Value $token")
        TextField(
            onValueChange = { input ->
                tokenInput.setTextAndPlaceCursorAtEnd(input)
                onTokenChange(input)
            },
            value = tokenInput.text.toString()
        )

        Text("Stored Stars $stars")
        Row {
            Button(onClick = onStarDecrement) {
                Text("Star--")
            }
            Button(onClick = onStarIncrement) {
                Text("Star++")
            }
        }

        Text("Stored Counter $counter")
        Row {
            Button(onClick = onCounterDecrement) {
                Text("Count--")
            }
            Button(onClick = onCounterIncrement) {
                Text("Count++")
            }
        }

        Text("Stored Reactive Count $reactiveCounterOne")
        Row {
            Button(onClick = onReactiveCounterOneDecrement) {
                Text("ReactCount--")
            }
            Button(onClick = onReactiveCounterOneIncrement) {
                Text("ReactCount++")
            }
        }

        Text("Stored Person $person")
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
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f, true),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DemoLocaleStrings()

        Button(onClick = onGetNews) {
            Text("Get News(Rest Client)", fontFamily = giffy())
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = onGetRepos) {
            Text("Get Top Repos(Apollo GraphQl Client)")
        }

        Spacer(Modifier.height(8.dp))

        val input = rememberTextFieldState("")
        TextField(input, lineLimits = TextFieldLineLimits.MultiLine(1, 1))

        Spacer(Modifier.height(8.dp))

        Button(onClick = { onInsertNews(input.text.toString()) }) {
            Text("Insert Fake News")
        }


        AnimatedVisibility(shouldShow) {
            //                val greeting = remember { Greeting().greet() }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painterResource(Res.drawable.compose_multiplatform),
                    null,
                    modifier = Modifier.size(80.dp, 80.dp)
                )
                LazyColumn {
                    item {
                        Text("Top 10", fontSize = 30.sp)
                        HorizontalDivider()
                        Spacer(Modifier.height(16.dp))
                    }

                    if (!news.isNullOrEmpty()) {
                        items(news) { story ->
                            Text(story.title.orEmpty())
                            Spacer(Modifier.height(8.dp))
                        }
                    } else if (!topRepos.isNullOrEmpty()) {
                        items(topRepos) { repo ->
                            Text(repo.name)
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun ColumnScope.DemoLocaleStrings() {
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