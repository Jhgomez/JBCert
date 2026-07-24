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

object Doggy {
    const val hola = "hola"
}


@Composable
@Preview
fun App(viewModel: MainViewModel = viewModel { MainViewModel() }) {
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
                    viewModel.getStories()
                }
            }) {
                Text("Click me!")
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
                            Text("Top 10 Hacker News Stories", fontSize = 30.sp)
                            HorizontalDivider()
                            Spacer(Modifier.height(16.dp))
                        }

                        items(state.value.stories) { story ->
                            Text(story.title.orEmpty())
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}