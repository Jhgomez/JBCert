package okik.tech.fullstack.feature.search.impl

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            val date = Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
            onDatePicked(date)
        }
    }

    SharedTransitionLayout {
        val scrollState: ScrollState = rememberScrollState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        LaunchedEffect(cacheKey, keyExtras) {
            Logger.logInfo("Coil cache", "Resource $resourceUrl cacheKey: $cacheKey")
            Logger.logInfo("Coil cache", "Resource $resourceUrl map: $keyExtras")
        }

        AnimatedContent(
            // this seems to work like a derived state of, so there is no need to create a derived state of explicitly
            targetState = sizeClass.minWidthDp >= 800
        ) { isLargeScreen ->

            Row(modifier = modifier) {
                if (isLargeScreen) {
                    Column(modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                    ) {
                        DatePickerDocked(
                            datePickerState = datePickerState,
                            localDate = selectedDate
                        )
                    }
                }

                SmallSearchScreen(
                    modifier = Modifier.fillMaxSize(),
                    resourceUrl = resourceUrl,
                    cacheKey = cacheKey,
                    animatedContentScope = this@AnimatedContent,
                    title = title,
                    date = date,
                    description = description,
                    copyright = copyright,
                    size = sizeClass,
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
private fun SharedTransitionScope.SmallSearchScreen(
    modifier: Modifier,
    resourceUrl: String?,
    cacheKey: MutableState<MemoryCache.Key?>,
    animatedContentScope: AnimatedContentScope,
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
        this@SmallSearchScreen.SmallSizeScreen(
            modifier = Modifier.fillMaxSize(),
            resourceUrl = resourceUrl,
            onSaveCoilCacheKey = { coilCacheKey ->
                if (cacheKey.value == null) cacheKey.value = coilCacheKey
            },
            coilCacheKey = { cacheKey.value },
            animatedContentScope = animatedContentScope,
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
                onDismiss = { showModal.value = false }
            )
        }
    }
}

@Composable
fun DatePickerDocked(
    datePickerState: DatePickerState,
    localDate: LocalDate?
) {
    OutlinedTextField(
        value = localDate.toString() ?: "",
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
            .width(400.dp)
            .height(64.dp)
    )

    DatePicker(
        state = datePickerState,
        showModeToggle = false,
        modifier = Modifier.requiredSize(width = 400.dp, height = 521.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    datePickerState: DatePickerState,
    onDismiss: () -> Unit
) {

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
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