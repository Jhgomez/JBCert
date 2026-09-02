package okik.tech.fullstack.feature.search.impl

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.memory.MemoryCache
import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.calendar
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import okik.tech.fullstack.Logger
import okik.tech.fullstack.feature.today.impl.SmallSizeScreen
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Instant

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    LaunchedEffect(null) {
        if (viewModel.state.localDate != null)
            viewModel.getApod(viewModel.state.localDate!!)
    }

    SearchScreen(
        modifier = modifier,
        resourceUrl = viewModel.state.apod?.hdUrl,
        onDatePicked = { epochDay -> if (epochDay != null) viewModel.getApod(epochDay) },
        title = viewModel.state.apod?.title,
        date = viewModel.state.apod?.date,
        description = viewModel.state.apod?.explanation,
        copyright = viewModel.state.apod?.copyright,
        selectedDate = viewModel.state.localDate,
    )
}

@Composable
fun SearchScreen(
    modifier: Modifier,
    resourceUrl: String?,
    onDatePicked: (LocalDate?) -> Unit,
    title: String?,
    date: String?,
    description: String?,
    copyright: String?,
    selectedDate: LocalDate?
) {
    val cacheKey: MutableState<MemoryCache.Key?> = remember { mutableStateOf(null) }
    val keyExtras: Map<String, String>? = remember { null }
    val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    var showModal = remember { mutableStateOf(false) }

    SharedTransitionLayout {
        val scrollState: ScrollState = rememberScrollState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        LaunchedEffect(cacheKey, keyExtras) {
            Logger.logInfo("Coil cache", "Resource $resourceUrl cacheKey: $cacheKey")
            Logger.logInfo("Coil cache", "Resource $resourceUrl map: $keyExtras")
        }

        AnimatedContent(
            targetState = sizeClass
        ) { size ->
            Box(modifier = modifier) {
                this@SharedTransitionLayout.SmallSizeScreen(
                    modifier = Modifier.fillMaxSize(),
                    resourceUrl = resourceUrl,
                    onSaveCoilCacheKey = { coilCacheKey ->
                        if (cacheKey.value == null) cacheKey.value = coilCacheKey
                    },
                    coilCacheKey = { cacheKey.value },
                    animatedContentScope = this@AnimatedContent,
                    title = title,
                    date = date,
                    description = description,
                    copyright = copyright,
                    sizeClass = size,
                    scrollState = scrollState,
                    scrollBehavior = scrollBehavior
                )

                MediumFloatingActionButton(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    onClick = { showModal.value = true },
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.calendar),
                        contentDescription = null
                    )
                }

                if (showModal.value) {
                    DatePickerModal(
                        initialEpochDay = selectedDate,
                        onDateSelected = onDatePicked,
                        onDismiss = { showModal.value = false }
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialEpochDay: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialEpochDay?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {

                onDateSelected(
                    datePickerState.selectedDateMillis?.let {
                        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
                    }
                )

                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}