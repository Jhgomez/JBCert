package okik.tech.fullstack

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource

import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.compose_multiplatform
import fullstack.app.shared.generated.resources.gallery
import fullstack.app.shared.generated.resources.home
import fullstack.app.shared.generated.resources.refresh
import okik.tech.fullstack.ui.ApodViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(viewModel: ApodViewModel = koinViewModel<ApodViewModel>()) {
    MaterialTheme {
        FullStackApp()
    }
}

@Composable
fun FullStackApp() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primary
            ) {

            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.gallery))
                },
                actions = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.refresh),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {

    }
}