package ru.fav.starlight.graph.ui.component

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

@Composable
fun LineGraph(values: List<Float>) {
    if (values.size < 2) return

    val max = values.max()
    val min = values.min()
    val range = max - min

    val lineColor = MaterialTheme.colorScheme.primary
    val gradientColor = lineColor.copy(alpha = 0.4f)
    val pointColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    val axisColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val selectedColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.8f)

    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(values) {
        selectedPointIndex = null
    }

    val normalizedPoints = remember(values) {
        values.mapIndexed { index, value ->
            val x = index.toFloat()
            val y = if (range == 0f) 0.5f else 1 - ((value - min) / range)
            Offset(x, y)
        }
    }

    val scaledPoints = remember(normalizedPoints, canvasSize) {
        val widthStep = if (values.size > 1) canvasSize.width / (values.size - 1).toFloat() else 0f
        normalizedPoints.mapIndexed { index, offset ->
            Offset(x = index * widthStep, y = offset.y * canvasSize.height)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .onSizeChanged { canvasSize = it }
            .pointerInput(scaledPoints) {
                detectTapGestures { tapOffset ->
                    val tappedIndex = scaledPoints.indexOfFirst {
                        (tapOffset - it).getDistance() < 40f
                    }
                    selectedPointIndex = if (tappedIndex != -1) tappedIndex else null
                }
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val horizontalLines = 4
            repeat(horizontalLines) { i ->
                val y = i * size.height / (horizontalLines - 1)
                drawLine(
                    color = axisColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2f
                )
            }

            scaledPoints.forEach { point ->
                drawLine(
                    color = axisColor,
                    start = Offset(point.x, 0f),
                    end = Offset(point.x, size.height),
                    strokeWidth = 2f
                )
            }

            val path = Path().apply {
                moveTo(scaledPoints.first().x, scaledPoints.first().y)
                for (point in scaledPoints.drop(1)) {
                    lineTo(point.x, point.y)
                }
            }

            val fillPath = Path().apply {
                addPath(path)
                lineTo(scaledPoints.last().x, size.height)
                lineTo(scaledPoints.first().x, size.height)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    endY = size.height
                )
            )

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )

            scaledPoints.forEachIndexed { index, point ->
                drawIntoCanvas { canvas ->
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 32f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }

                    canvas.nativeCanvas.drawText(
                        (index + 1).toString(),
                        point.x,
                        size.height + 50f,
                        paint
                    )
                }
            }

            scaledPoints.forEachIndexed { index, point ->
                drawCircle(
                    color = if (selectedPointIndex == index) selectedColor else pointColor,
                    radius = 16f,
                    center = point
                )
            }

            selectedPointIndex?.let { index ->
                val selectedPoint = scaledPoints[index]
                val valueText = "${values[index]}"
                val padding = 48f

                drawIntoCanvas { canvas ->
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 40f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }

                    val textWidth = paint.measureText(valueText)
                    val textHeight = paint.descent() - paint.ascent()

                    val backgroundRect = RectF(
                        selectedPoint.x - textWidth / 2f - padding / 2,
                        selectedPoint.y - textHeight - 55f,
                        selectedPoint.x + textWidth / 2f + padding / 2,
                        selectedPoint.y - 25f
                    )

                    val backgroundPaint = android.graphics.Paint().apply {
                        color = selectedColor.toArgb()
                        style = android.graphics.Paint.Style.FILL
                    }

                    canvas.nativeCanvas.drawRoundRect(backgroundRect, 12f, 12f, backgroundPaint)

                    val textY = backgroundRect.centerY() - (paint.ascent() + paint.descent()) / 2f
                    canvas.nativeCanvas.drawText(
                        valueText,
                        selectedPoint.x,
                        textY,
                        paint
                    )
                }
            }
        }
    }
}
