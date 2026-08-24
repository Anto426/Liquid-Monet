package com.anto426.antoui.components.cards

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.antoui.components.feedback.AntoLinearProgressIndicator
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle

/**
 * AntoMediaController - Radiant Optical Liquid Glass Media Player Widget with Tactile Bubble Controls.
 */
@Composable
fun AntoMediaController(
    title: String,
    artist: String,
    isPlaying: Boolean,
    progress: Float,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentTime: String = "1:24",
    totalTime: String = "3:45",
    onContainerClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary
    val hostContentBackdrop = LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> backdrop
    }

    val shape = RoundedRectangle(28.dp)
    val albumShape = RoundedRectangle(16.dp)
    val controlShape = Capsule()

    // Vibrant infinite breathing pulse while music is playing
    val infiniteTransition = rememberInfiniteTransition(label = "MusicPlaybackPulse")
    val playPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PlayPulse"
    )

    // Touch feedback highlights
    val containerHighlight = rememberAntoControlHighlight()
    val albumHighlight = rememberAntoControlHighlight()
    val playHighlight = rememberAntoControlHighlight()
    val prevHighlight = rememberAntoControlHighlight()
    val nextHighlight = rememberAntoControlHighlight()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .antoLiquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = AntoGlassRole.Surface,
                layerBlock = if (onContainerClick != null) antoControlLayerBlock(enabled, containerHighlight) else null
            )
            .then(
                if (onContainerClick != null) {
                    Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            enabled = enabled,
                            onClick = { onContainerClick.invoke() }
                        )
                        .antoControlPressFeedback(enabled, containerHighlight)
                } else Modifier
            )
            .padding(18.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Vibrant Glowing Album Artwork Bubble
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .graphicsLayer(antoControlLayerBlock(enabled, albumHighlight) ?: {})
                        .antoControlPressFeedback(
                            enabled = enabled,
                            interactiveHighlight = albumHighlight,
                            drawHighlightOverlay = false
                        )
                        .drawBehind {
                            val glowRadius = size.maxDimension * 0.65f
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.45f),
                                        primaryColor.copy(alpha = 0f)
                                    ),
                                    center = center,
                                    radius = glowRadius
                                ),
                                radius = glowRadius,
                                center = center
                            )
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            enabled = enabled,
                            onClick = onPlayPauseClick
                        )
                        .antoLiquidGlass(
                            backdrop = effectiveBackdrop,
                            shape = albumShape,
                            role = AntoGlassRole.Navigation,
                            containerColor = primaryColor.copy(alpha = 0.55f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AntoIcons.MusicNote,
                        contentDescription = "Album",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    BasicText(
                        text = title,
                        style = TextStyle(
                            color = colorScheme.onSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    BasicText(
                        text = artist,
                        style = TextStyle(
                            color = colorScheme.onSurface.copy(alpha = 0.70f),
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Linear Progress Indicator
            AntoLinearProgressIndicator(
                progress = progress,
                backdropState = effectiveBackdrop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BasicText(
                    text = currentTime,
                    style = TextStyle(
                        color = colorScheme.onSurface.copy(alpha = 0.65f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                BasicText(
                    text = totalTime,
                    style = TextStyle(
                        color = colorScheme.onSurface.copy(alpha = 0.65f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Playback Controls with Liquid Optical Bubble Pods
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Bubble Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer(antoControlLayerBlock(enabled, prevHighlight) ?: {})
                        .antoControlPressFeedback(
                            enabled = enabled,
                            interactiveHighlight = prevHighlight,
                            drawHighlightOverlay = false
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            enabled = enabled,
                            onClick = onPreviousClick
                        )
                        .antoLiquidGlass(
                            backdrop = effectiveBackdrop,
                            shape = controlShape,
                            role = AntoGlassRole.Navigation,
                            containerColor = Color.White.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AntoIcons.SkipPrevious,
                        contentDescription = "Precedente",
                        tint = colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Vibrant Radiant Play / Pause Bubble with Blooming Radial Halo
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .graphicsLayer(antoControlLayerBlock(enabled, playHighlight) ?: {})
                        .antoControlPressFeedback(
                            enabled = enabled,
                            interactiveHighlight = playHighlight,
                            drawHighlightOverlay = false
                        )
                        .drawBehind {
                            val activePulse = if (isPlaying) playPulse else 0f
                            val glowRadius = size.maxDimension * (0.65f + 0.15f * activePulse)
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.50f + 0.18f * activePulse),
                                        primaryColor.copy(alpha = 0f)
                                    ),
                                    center = center,
                                    radius = glowRadius
                                ),
                                radius = glowRadius,
                                center = center
                            )
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            enabled = enabled,
                            onClick = onPlayPauseClick
                        )
                        .antoLiquidGlass(
                            backdrop = effectiveBackdrop,
                            shape = controlShape,
                            role = AntoGlassRole.Navigation,
                            containerColor = primaryColor.copy(alpha = 0.85f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPlaying) {
                        Icon(
                            imageVector = AntoIcons.Pause,
                            contentDescription = "Pausa",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Icon(
                            imageVector = AntoIcons.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Next Bubble Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer(antoControlLayerBlock(enabled, nextHighlight) ?: {})
                        .antoControlPressFeedback(
                            enabled = enabled,
                            interactiveHighlight = nextHighlight,
                            drawHighlightOverlay = false
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            enabled = enabled,
                            onClick = onNextClick
                        )
                        .antoLiquidGlass(
                            backdrop = effectiveBackdrop,
                            shape = controlShape,
                            role = AntoGlassRole.Navigation,
                            containerColor = Color.White.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AntoIcons.SkipNext,
                        contentDescription = "Successivo",
                        tint = colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
