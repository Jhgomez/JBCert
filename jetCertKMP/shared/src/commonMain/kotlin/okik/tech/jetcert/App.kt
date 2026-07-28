package okik.tech.jetcert

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.painterResource

import jetcert.shared.generated.resources.Res
import jetcert.shared.generated.resources.compose_multiplatform
import kotlinx.coroutines.launch
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
            Button(onClick = {
                coroutineScope.launch {
//                    viewModel.getStories()
                    viewModel.insertNewsToDB()
                    viewModel.insertFakeNews("Hola")
                }
            }) {
                Text("Get Stories(Rest Client)")
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

            AnimatedVisibility(state.value.showContent) {
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
                                Text(repo.repo?.onRepository?.name ?: "name was null")
                                Spacer(Modifier.height(8.dp))
                            }
                        }

                    }
                }
            }
        }
    }
}