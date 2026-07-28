package okik.tech.jetcert

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.client.request.invoke
import org.jetbrains.compose.resources.painterResource

import jetcert.shared.generated.resources.Res
import jetcert.shared.generated.resources.allPluralStringResources
import jetcert.shared.generated.resources.app_name
import jetcert.shared.generated.resources.compose_multiplatform
import jetcert.shared.generated.resources.plural_news
import jetcert.shared.generated.resources.string_template
import jetcert.shared.generated.resources.top_news
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringArrayResource
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
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DemoLocaleStrings()

            Button(onClick = {
                coroutineScope.launch {
//                    viewModel.getStories()
                    viewModel.insertNewsToDB()
                }
            }) {
                Text("Get News(Rest Client)", fontFamily = giffy())
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                coroutineScope.launch {
//                    viewModel.getTopRepos()
                    viewModel.insertTopRepos()
                }
            }) {
                Text("Get Top Repos(Apollo GraphQl Client)")
            }

            Spacer(Modifier.height(8.dp))

            val input = rememberTextFieldState("")
            TextField(input, lineLimits = TextFieldLineLimits.MultiLine(1, 1))

            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                coroutineScope.launch {
                    viewModel.insertFakeNews(input.text.toString())
                }
            }) {
                Text("Insert Fake News")
            }


            AnimatedVisibility(!state.value.stories.isNullOrEmpty() || !state.value.topRepos.isNullOrEmpty()) {
//                val greeting = remember { Greeting().greet() }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    LazyColumn {
                        item {
                            Text("Top 10", fontSize = 30.sp)
                            HorizontalDivider()
                            Spacer(Modifier.height(16.dp))
                        }

                        if (!state.value.stories.isNullOrEmpty()) {
                            items(state.value.stories!!) { story ->
                                Text(story.title.orEmpty())
                                Spacer(Modifier.height(8.dp))
                            }
                        } else if (!state.value.topRepos.isNullOrEmpty()) {
                            items(state.value.topRepos!!) { repo ->
                                Text(repo.name)
                                Spacer(Modifier.height(8.dp))
                            }
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

    Text(text = "Array[0]: ${stringArrayResource(resource = Res.array.top_news)[0]}")
    Spacer(Modifier.height(4.dp))

    Text(text = "Array[0]: ${stringArrayResource(resource = Res.array.top_news)[1]}")
    Spacer(Modifier.height(4.dp))
}