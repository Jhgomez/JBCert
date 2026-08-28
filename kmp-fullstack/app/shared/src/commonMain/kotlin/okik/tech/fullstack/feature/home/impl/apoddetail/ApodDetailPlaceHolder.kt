package okik.tech.fullstack.feature.home.impl.apoddetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.apod_list_placeholder_message
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeDetailPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryFixedDim),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.apod_list_placeholder_message),
            style = MaterialTheme.typography.titleLargeEmphasized
        )
    }
}