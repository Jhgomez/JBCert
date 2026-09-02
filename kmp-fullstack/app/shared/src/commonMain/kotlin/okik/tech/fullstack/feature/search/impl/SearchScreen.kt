package okik.tech.fullstack.feature.search.impl

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
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
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
    )

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
            val isSmallScreen = size.minWidthDp <= 800

            Row(modifier = modifier) {
                if (!isSmallScreen) {
                    Column(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.4f)) {
                        DatePickerDocked(
                            datePickerState = datePickerState,
                            onDateSelected = onDatePicked
                        )
                    }
                }

                SmallSearchScreen(
                    modifier = Modifier.fillMaxSize(),
                    scope = this@SharedTransitionLayout,
                    resourceUrl = resourceUrl,
                    cacheKey = cacheKey,
                    scope2 = this@AnimatedContent,
                    title = title,
                    date = date,
                    description = description,
                    copyright = copyright,
                    size = size,
                    scrollState = scrollState,
                    scrollBehavior = scrollBehavior,
                    showModal = showModal,
                    datePickerState = datePickerState,
                    onDatePicked = onDatePicked
                )
            }
        }
    }
}

@Composable
private fun SmallSearchScreen(
    modifier: Modifier,
    scope: SharedTransitionScope,
    resourceUrl: String?,
    cacheKey: MutableState<MemoryCache.Key?>,
    scope2: AnimatedContentScope,
    title: String?,
    date: String?,
    description: String?,
    copyright: String?,
    size: WindowSizeClass,
    scrollState: ScrollState,
    scrollBehavior: TopAppBarScrollBehavior,
    showModal: MutableState<Boolean>,
    datePickerState: DatePickerState,
    onDatePicked: (LocalDate?) -> Unit
) {

    Box(modifier = modifier) {
        scope.SmallSizeScreen(
            modifier = Modifier.fillMaxSize(),
            resourceUrl = resourceUrl,
            onSaveCoilCacheKey = { coilCacheKey ->
                if (cacheKey.value == null) cacheKey.value = coilCacheKey
            },
            coilCacheKey = { cacheKey.value },
            animatedContentScope = scope2,
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
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.calendar),
                contentDescription = null
            )
        }

        if (showModal.value) {
            DatePickerModal(
                datePickerState = datePickerState,
                onDateSelected = onDatePicked,
                onDismiss = { showModal.value = false }
            )
        }
    }
}

@Composable
fun DatePickerDocked(
    datePickerState: DatePickerState,
    onDateSelected: (LocalDate?) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = datePickerState.selectedDateMillis?.let {
                val date = Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
                onDateSelected(date)
                date.toString()
            } ?: "",
            onValueChange = { },
            label = { Text("DOB") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = vectorResource(Res.drawable.calendar),
                    contentDescription = "Select date"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 64.dp)
                .shadow(elevation = 4.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    datePickerState: DatePickerState,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {

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

@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen(
        modifier = Modifier.fillMaxSize(),
        resourceUrl = null,
        onDatePicked = { },
        title = null,
        date = null,
        description = null,
        copyright = null,
        selectedDate = LocalDate(2025, 11, 15)
    )
}