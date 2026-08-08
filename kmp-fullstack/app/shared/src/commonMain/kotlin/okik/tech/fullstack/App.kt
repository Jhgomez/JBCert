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
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
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
import fullstack.app.shared.generated.resources.info
import fullstack.app.shared.generated.resources.refresh
import fullstack.app.shared.generated.resources.search
import fullstack.app.shared.generated.resources.today
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

private var cachedColors: NavigationBarItemColors? = null

val ColorScheme.navigationBatItemColors: NavigationBarItemColors
    @Composable
    get() =  cachedColors ?:
        NavigationBarItemColors(
            selectedIconColor = Color.White,
            selectedTextColor = Color.White,
            selectedIndicatorColor = onPrimaryContainer,
            unselectedIconColor = Color.LightGray,
            unselectedTextColor = Color.LightGray,
            disabledIconColor = Color.DarkGray,
            disabledTextColor = Color.DarkGray
        ).also {
            cachedColors = it
        }

@Composable
fun FullStackApp() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.home),
                            contentDescription = null
                        )
                    },
                    selected = true,
                    onClick = {},
                    label = {
                        Text("Home")
                    },
                    colors = MaterialTheme.colorScheme.navigationBatItemColors
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.today),
                            contentDescription = null
                        )
                    },
                    selected = false,
                    onClick = {},
                    label = {
                        Text("Today")
                    },
                    colors = MaterialTheme.colorScheme.navigationBatItemColors
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.search),
                            contentDescription = null
                        )
                    },
                    selected = false,
                    onClick = {},
                    label = {
                        Text("Search")
                    },
                    colors = MaterialTheme.colorScheme.navigationBatItemColors
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.info),
                            contentDescription = null
                        )
                    },
                    selected = false,
                    onClick = {},
                    label = {
                        Text("About")
                    },
                    colors = MaterialTheme.colorScheme.navigationBatItemColors
                )
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