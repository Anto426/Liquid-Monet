package com.kyant.shapes

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Capsule(): CornerBasedShape = RoundedCornerShape(50)

fun RoundedRectangle(cornerRadius: Dp = 0.dp): CornerBasedShape = RoundedCornerShape(cornerRadius)
