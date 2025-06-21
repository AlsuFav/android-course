package ru.fav.starlight.diagram.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ru.fav.starlight.diagram.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.core.graphics.toColorInt
import ru.fav.starlight.utils.extensions.dpToPx
import ru.fav.starlight.utils.extensions.spToPx

class DiagramView @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0,
) : View(ctx, attrs, defStyleAttrs) {

    private var diagramData: DiagramData? = null
        set(value) {
            field = value
            invalidate()
        }
    private var lineColor: Int = Color.GRAY
        set(value) {
            field = value
            invalidate()
        }

    private var selectedSectorIndex = -1

    private var centerX = 0f
    private var centerY = 0f
    private var maxRadius = 0f
    private var ringWidth = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = context.spToPx(18f)
        typeface = Typeface.DEFAULT_BOLD
    }
    private val textOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = context.spToPx(18f)
        typeface = Typeface.DEFAULT_BOLD
        style = Paint.Style.STROKE
        strokeWidth = context.dpToPx(3f)
    }

    private val minSectors = 2
    private val maxSectors = 7
    private val minValue = 1
    private val maxValue = 100
    private val startAngle = 90f
    private val fullCircle = 360f
    private val dotOffsetAngle = 5f
    private val lightenFactor = 0.3f

    private val rectF = RectF()

    private val percentageWidth = context.dpToPx(72f)
    private val ringPadding = context.dpToPx(3f)
    private val centerClearanceRadius = context.dpToPx(16f)
    private val viewPadding = context.dpToPx(8f)
    private val dotRadius = context.dpToPx(3f)

    init {
        isClickable = true

        attrs?.let { attributeSet ->
            val typedArray = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.DiagramView,
                defStyleAttrs,
                0
            )

            try {
                lineColor = typedArray.getColor(
                    R.styleable.DiagramView_line_color,
                    Color.GRAY.toInt()
                )

                val valuesString = typedArray.getString(R.styleable.DiagramView_sector_values)
                val colorsString = typedArray.getString(R.styleable.DiagramView_sector_colors)

                if (!valuesString.isNullOrEmpty() && !colorsString.isNullOrEmpty()) {
                    val sectors = parseSectorData(valuesString, colorsString)
                    diagramData = if (sectors != null) DiagramData(sectors) else null
                }
            } finally {
                typedArray.recycle()
            }
        }
    }

    private fun parseSectorData(valuesString: String, colorsString: String): List<DiagramSector>? {
        val values = valuesString.split(",").mapNotNull {
            it.trim().toIntOrNull()
        }

        val colors = colorsString.split(",").mapNotNull { colorStr ->
            try {
                val trimmedColor = colorStr.trim()
                val colorWithHash = if (!trimmedColor.startsWith("#") && trimmedColor.length == 6) {
                    "$#$trimmedColor"
                } else {
                    trimmedColor
                }
                colorWithHash.toColorInt()
            } catch (_: IllegalArgumentException) {
                null
            }
        }

        if (!validateSectorData(values, colors)) return null

        return values.zip(colors) { value, color ->
            DiagramSector(value, color)
        }
    }

    fun updateData(values: List<Int>, colors: List<Int>) {
        if (!validateSectorData(values, colors)) return

        diagramData = DiagramData(values.zip(colors) { value, color ->
            DiagramSector(value, color)
        })
    }

    private fun validateSectorData(values: List<Int>, colors: List<Int>) : Boolean {
        if (values.isEmpty() || values.size < minSectors ||
            values.size > maxSectors || values.any { v -> v < minValue || v > maxValue }) {
            return false
        }

        val uniqueColors = colors.distinct()
        if (values.size != uniqueColors.size || uniqueColors.size != colors.size) {
            return false
        }

        return true
    }

    private fun selectSector(index: Int) {
        diagramData?.let { data ->
            selectedSectorIndex = index
            data.sectors.forEachIndexed { i, sector ->
                sector.isSelected = (i == index)
            }
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = (w - percentageWidth) / 2f
        centerY = h / 2f
        maxRadius = minOf(centerX, centerY) - viewPadding

        val totalPaddingSpace = (maxSectors - 1) * ringPadding
        val availableSpace = maxRadius - centerClearanceRadius - totalPaddingSpace
        ringWidth = availableSpace / maxSectors
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val data = diagramData ?: return

        data.sectors.forEachIndexed { index, sector ->
            drawRing(canvas, sector, index)
        }

        data.sectors.forEachIndexed { index, sector ->
            if (index != selectedSectorIndex) {
                drawPercentage(canvas, sector, index)
            }
        }

        if (selectedSectorIndex != -1) {
            data.sectors.getOrNull(selectedSectorIndex)?.let { selectedSector ->
                drawPercentage(canvas, selectedSector, selectedSectorIndex)
            }
        }
    }

    private fun drawRing(canvas: Canvas, sector: DiagramSector, ringIndex: Int) {
        val innerRadius = centerClearanceRadius + (ringIndex * (ringWidth + ringPadding))

        val color = if (sector.isSelected) {
            lightenColor(sector.color, lightenFactor)
        } else {
            sector.color
        }

        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = ringWidth

        val sweepAngle = fullCircle * sector.value / maxValue

        val ringCenterRadius = innerRadius + ringWidth / 2f

        rectF.set(
            centerX - ringCenterRadius,
            centerY - ringCenterRadius,
            centerX + ringCenterRadius,
            centerY + ringCenterRadius
        )

        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)

        val dotAngle = calculateDotAngle(sweepAngle)
        val dotX = centerX + (ringCenterRadius * cos(dotAngle)).toFloat()
        val dotY = centerY + (ringCenterRadius * sin(dotAngle)).toFloat()

        paint.color = lineColor
        paint.style = Paint.Style.FILL
        canvas.drawCircle(dotX, dotY, dotRadius, paint)
    }

    private fun drawPercentage(canvas: Canvas, sector: DiagramSector, ringIndex: Int) {
        val percentageX = width - percentageWidth + context.dpToPx(30f)

        val innerRadius = centerClearanceRadius + (ringIndex * (ringWidth + ringPadding))
        val ringCenterRadius = innerRadius + ringWidth / 2f

        val sweepAngle = fullCircle * sector.value / maxValue
        val dotAngle = calculateDotAngle(sweepAngle)

        val dotX = centerX + (ringCenterRadius * cos(dotAngle)).toFloat()
        val dotY = centerY + (ringCenterRadius * sin(dotAngle)).toFloat()

        paint.color = lineColor
        paint.strokeWidth = context.dpToPx(1f)
        paint.style = Paint.Style.STROKE

        val lineEndX = percentageX - context.dpToPx(10f)
        canvas.drawLine(dotX, dotY, lineEndX, dotY, paint)

        paint.style = Paint.Style.FILL
        canvas.drawCircle(lineEndX, dotY, dotRadius, paint)

        val percentText = "${sector.value}%"
        val textY = dotY + textPaint.textSize / 3

        textOutlinePaint.color = Color.WHITE
        canvas.drawText(percentText, percentageX, textY, textOutlinePaint)

        textPaint.color = if(sector.isSelected) lightenColor(sector.color, lightenFactor) else sector.color
        canvas.drawText(percentText, percentageX, textY, textPaint)
    }

    private fun calculateDotAngle(sweepAngle: Float): Double {
        val desiredAngle = startAngle + sweepAngle - dotOffsetAngle

        val finalAngle = if (desiredAngle < startAngle) {
            startAngle
        } else {
            desiredAngle
        }

        return Math.toRadians(finalAngle.toDouble())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val data = diagramData ?: return false

            val touchX = event.x
            val touchY = event.y

            val clickedSectorIndex = findClickedSector(touchX, touchY, data.sectors)
            selectSector(clickedSectorIndex)
            return true
        }

        return super.onTouchEvent(event)
    }

    private fun findClickedSector(x: Float, y: Float, sectors: List<DiagramSector>): Int {
        val dx = x - centerX
        val dy = y - centerY
        val distance = sqrt(dx * dx + dy * dy)

        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        angle = (angle + fullCircle - startAngle) % fullCircle

        sectors.forEachIndexed { index, sector ->
            val innerRadius = centerClearanceRadius + (index * (ringWidth + ringPadding))
            val outerRadius = innerRadius + ringWidth

            if (distance in innerRadius..outerRadius) {
                val sweepAngle = fullCircle * sector.value / maxValue
                if (angle <= sweepAngle) {
                    return index
                }
            }
        }

        return -1
    }

    private fun lightenColor(color: Int, factor: Float): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        val newRed = (red + (255 - red) * factor).toInt().coerceIn(0, 255)
        val newGreen = (green + (255 - green) * factor).toInt().coerceIn(0, 255)
        val newBlue = (blue + (255 - blue) * factor).toInt().coerceIn(0, 255)

        return Color.rgb(newRed, newGreen, newBlue)
    }
}