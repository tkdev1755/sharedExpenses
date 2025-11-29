package com.mds.sharedexpenses.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme

// a simple animated border around all specified composable childrens
@Composable
fun AnimatedBorderCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(size = 16.dp),
    borderWidth: Dp = 1.5.dp,
    gradient: Brush = Brush.sweepGradient(
        listOf(
            Color.White,
            MaterialTheme.colorScheme.primary,
            Color.White
        )
    ),
    animationDuration: Int = 3000,
    onCardClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite Color Animation")
    val degrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Infinite Colors"
    )
    Surface(
        modifier = modifier
            .clip(shape)
            .clickable { onCardClick() },
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    rotate(degrees = degrees) {
                        drawCircle(
                            brush = gradient,
                            radius = size.maxDimension,
                            blendMode = androidx.compose.ui.graphics.BlendMode.SrcOver, // Standard BlendMode
                        )
                    }
                    drawContent()
                }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(borderWidth),
                shape = shape,
                color = MaterialTheme.colorScheme.surface
            ) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedBorderCardPreview() {
    SharedExpensesTheme {
        AnimatedBorderCard(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Featured Group",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "fancy gradient!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
