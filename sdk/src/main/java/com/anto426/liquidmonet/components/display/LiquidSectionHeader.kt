package com.anto426.liquidmonet.components.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.theme.LiquidGlassTheme

/**
 * Lightweight heading for a related group of Liquid Monet components.
 *
 * The optional [subtitle] explains the group without wrapping it in another
 * surface, keeping the hierarchy used by Android settings screens.
 */
@Composable
fun LiquidSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    titleColor: Color = LiquidGlassTheme.colors.content,
    subtitleColor: Color = LiquidGlassTheme.colors.secondaryContent
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { heading() }
            .padding(start = 4.dp, end = 4.dp, top = 10.dp, bottom = 2.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = title,
            color = titleColor,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                color = subtitleColor,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
