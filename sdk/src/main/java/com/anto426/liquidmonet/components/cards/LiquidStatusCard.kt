package com.anto426.liquidmonet.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle

import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.kyant.shapes.Capsule

enum class LiquidStatusType {
    Info, Success, Warning, Error
}

/**
 * LiquidStatusCard - Crystal Liquid Glass Informative Status Card.
 * Uses pure optical refraction with dedicated liquid glass icon bubble pods.
 */
@Composable
fun LiquidStatusCard(
    title: String,
    description: String,
    statusType: LiquidStatusType = LiquidStatusType.Info,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val statusColor = when (statusType) {
        LiquidStatusType.Info -> MaterialTheme.colorScheme.primary
        LiquidStatusType.Success -> Color(0xFF00C853)
        LiquidStatusType.Warning -> Color(0xFFFFAB00)
        LiquidStatusType.Error -> MaterialTheme.colorScheme.error
    }

    val contentColor = MaterialTheme.colorScheme.onSurface
    val iconHighlight = com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight()
    val hostContentBackdrop = com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        backdrop != emptyBackdrop() -> backdrop
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> emptyBackdrop()
    }

    LiquidCard(
        modifier = modifier.fillMaxWidth(),
        backdropState = effectiveBackdrop,
        shape = RoundedRectangle(22.dp),
        containerColor = null,
        contentPadding = 16.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Optical Liquid Glass Icon Bubble Pod with Jelly Bounce
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .graphicsLayer(com.anto426.liquidmonet.components.internal.liquidControlLayerBlock(true, iconHighlight) ?: {})
                    .liquidControlPressFeedback(
                        enabled = true,
                        interactiveHighlight = iconHighlight,
                        drawHighlightOverlay = false
                    )
                    .liquidGlass(
                        backdrop = effectiveBackdrop,
                        shape = Capsule(),
                        role = LiquidGlassRole.Navigation,
                        containerColor = statusColor.copy(alpha = 0.14f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (statusType) {
                        LiquidStatusType.Info -> LiquidIcons.Info
                        LiquidStatusType.Success -> LiquidIcons.Check
                        LiquidStatusType.Warning -> LiquidIcons.Warning
                        LiquidStatusType.Error -> LiquidIcons.Close
                    },
                    contentDescription = statusType.name,
                    tint = statusColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.72f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
