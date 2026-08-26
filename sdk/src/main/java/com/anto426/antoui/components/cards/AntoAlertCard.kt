package com.anto426.antoui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anto426.antoui.components.buttons.AntoButton
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

enum class AntoAlertType {
    Info, Success, Warning, Error
}

/** Dynamic Liquid Glass alert for contextual notices, deadlines and actions. */
@Composable
fun AntoAlertCard(
    title: String,
    message: String,
    type: AntoAlertType = AntoAlertType.Info,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    supportingText: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val (accent, defaultIcon) = when (type) {
        AntoAlertType.Info -> MaterialTheme.colorScheme.primary to AntoIcons.Info
        AntoAlertType.Success -> Color(0xFF00C853) to AntoIcons.Check
        AntoAlertType.Warning -> Color(0xFFFFAB00) to AntoIcons.Warning
        AntoAlertType.Error -> MaterialTheme.colorScheme.error to AntoIcons.Close
    }

    AntoCard(
        modifier = modifier,
        backdropState = backdropState,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(44.dp)
                    .antoLiquidGlass(
                        backdrop = backdropState,
                        shape = Capsule(),
                        role = AntoGlassRole.Navigation,
                        containerColor = accent.copy(alpha = 0.16f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon ?: defaultIcon,
                    contentDescription = type.name,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
                )
                supportingText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
                if (actionLabel != null && onAction != null) {
                    AntoButton(
                        text = actionLabel,
                        onClick = onAction,
                        backdropState = backdropState,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
