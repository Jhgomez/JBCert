package okik.tech.fullstack

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.gallery
import fullstack.app.shared.generated.resources.home
import fullstack.app.shared.generated.resources.info
import fullstack.app.shared.generated.resources.refresh
import fullstack.app.shared.generated.resources.search
import fullstack.app.shared.generated.resources.today
import okik.tech.fullstack.ui.ApodViewModel
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

private var cachedNavItemColors: NavigationBarItemColors? = null

val ColorScheme.navigationBatItemColors: NavigationBarItemColors
    @Composable
    get() =  cachedNavItemColors ?:
        NavigationBarItemColors(
            selectedIconColor = Color.White,
            selectedTextColor = Color.White,
            selectedIndicatorColor = onPrimaryContainer,
            unselectedIconColor = Color.LightGray,
            unselectedTextColor = Color.LightGray,
            disabledIconColor = Color.DarkGray,
            disabledTextColor = Color.DarkGray
        ).also {
            cachedNavItemColors = it
        }

private var cachedTopBarColors: TopAppBarColors? = null

val ColorScheme.topAppBarCustomColors: TopAppBarColors
    @Composable
    get() = cachedTopBarColors ?:
        TopAppBarColors(
            containerColor = primary,
            scrolledContainerColor = primaryContainer,
            navigationIconContentColor = onPrimary,
            titleContentColor = onPrimary,
            actionIconContentColor = onPrimary,
            subtitleContentColor = onPrimary,
        ).also {
            cachedTopBarColors = it
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
                },
                colors = MaterialTheme.colorScheme.topAppBarCustomColors
            )
        }
    ) {

    }
}