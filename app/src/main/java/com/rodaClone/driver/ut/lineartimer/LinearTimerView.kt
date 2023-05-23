package com.rodaClone.driver.ut.lineartimer

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.rodaClone.driver.R
import java.lang.Exception
import java.lang.NullPointerException

/**
 * Created by kartikeykushwaha on 18/12/16.
 */
class LinearTimerView(
    context: Context?,
    attrs: AttributeSet?
) : View(context, attrs) {
    private var arcPaint: Paint? = null
    private var rectF: RectF? = null
    private var initialColor = 0
    private var progressColor = 0
    private var circleRadiusInDp = 0
    private var strokeWidthInDp = 0

    // The point from where the color-fill animation will start.
    private var startingPoint = 270
    /**
     * Method to get the degrees up-till which the arc is already pre-filled.
     *
     * @return pre fill angle
     */
    /**
     * Sets pre fill angle.
     *
     * @param preFillAngle the pre fill angle
     */
    // The point up-till which user wants the circle to be pre-filled.
    var preFillAngle = 0f

    /**
     * Define the size of the circle, prepare it's measurement and style.
     */
    protected fun init() {
        rectF = RectF(
            convertDpIntoPixel(strokeWidthInDp.toFloat()).toInt().toFloat(),
            convertDpIntoPixel(strokeWidthInDp.toFloat()).toInt().toFloat(),
            (convertDpIntoPixel((circleRadiusInDp * 2).toFloat()).toInt() + convertDpIntoPixel(
                strokeWidthInDp.toFloat()
            ).toInt()).toFloat(),
            (convertDpIntoPixel((circleRadiusInDp * 2).toFloat()).toInt() + convertDpIntoPixel(
                strokeWidthInDp.toFloat()
            ).toInt()).toFloat()
        )
        arcPaint = Paint()
        arcPaint!!.isAntiAlias = true
        arcPaint!!.style = Paint.Style.STROKE
        arcPaint!!.setStrokeWidth(convertDpIntoPixel(strokeWidthInDp.toFloat()))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        try {
            // Grey Circle - This circle will be there by default.
            arcPaint!!.color = initialColor
            canvas.drawCircle(
                rectF!!.centerX(), rectF!!.centerY(),
                convertDpIntoPixel(circleRadiusInDp.toFloat()), arcPaint!!
            )

            // Green Arc (Arc with 360 angle) - This circle will be animated as time progresses.
            arcPaint!!.color = progressColor
            canvas.drawArc(rectF!!, startingPoint.toFloat(), preFillAngle, false, arcPaint!!)
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = convertDpIntoPixel(circleRadiusInDp.toFloat()).toInt()
        val desiredWidth = convertDpIntoPixel(circleRadiusInDp.toFloat()).toInt()
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var finalWidth: Int
        var finalHeight: Int

        // Measure Width
        finalWidth = if (widthMode == MeasureSpec.EXACTLY) {
            // Must be this size
            widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            Math.min(desiredWidth, widthSize)
        } else {
            // Be whatever you want
            desiredWidth
        }

        // Measure Height
        finalHeight = if (heightMode == MeasureSpec.EXACTLY) {
            // Must be this size
            heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            Math.min(desiredHeight, heightSize)
        } else {
            // Be whatever you want
            desiredHeight
        }
        finalHeight = (finalHeight + convertDpIntoPixel(strokeWidthInDp.toFloat()).toInt()) * 2
        finalWidth = (finalWidth + convertDpIntoPixel(strokeWidthInDp.toFloat()).toInt()) * 2
        setMeasuredDimension(finalWidth, finalHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    /**
     * Get the current set initial color.
     * @return
     */
    fun getInitialColor(): Int {
        return initialColor
    }

    /**
     * Method to set the initial color programmatically.
     * @param initialColor
     */
    fun setInitialColor(initialColor: Int) {
        this.initialColor = initialColor

        // Redraw the view.
        invalidate()
        requestLayout()
        init()
    }

    /**
     * Get the current set progress color.
     * @return
     */
    fun getProgressColor(): Int {
        return progressColor
    }

    /**
     * Method to set the progress color programmatically.
     * @param progressColor
     */
    fun setProgressColor(progressColor: Int) {
        this.progressColor = progressColor

        // Redraw the view.
        invalidate()
        requestLayout()
        init()
    }

    /**
     * Get the current radius.
     * @return
     */
    fun getCircleRadiusInDp(): Int {
        return circleRadiusInDp
    }

    /**
     * Method to set the radius programmatically.
     * @param circleRadiusInDp
     */
    fun setCircleRadiusInDp(circleRadiusInDp: Int) {
        this.circleRadiusInDp = circleRadiusInDp

        // Redraw the view.
        invalidate()
        requestLayout()
        init()
    }

    /**
     * Get the current width of the stroke.
     * @return
     */
    fun getStrokeWidthInDp(): Int {
        return strokeWidthInDp
    }

    /**
     * Method to set the stroke width programmatically.
     * @param strokeWidthInDp
     */
    fun setStrokeWidthInDp(strokeWidthInDp: Int) {
        this.strokeWidthInDp = strokeWidthInDp

        // Redraw the view.
        invalidate()
        requestLayout()
        init()
    }

    /**
     * Get the current starting point.
     * @return
     */
    fun getStartingPoint(): Int {
        return startingPoint
    }

    /**
     * Method to set the starting point programmatically.
     * @param startingPoint
     */
    fun setStartingPoint(startingPoint: Int) {
        this.startingPoint = startingPoint

        // Redraw the view.
        invalidate()
        requestLayout()
        init()
    }

    /**
     * Method to convert DPs into Pixels.
     */
    private fun convertDpIntoPixel(dp: Float): Float {
        val resources = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            resources.displayMetrics
        )
    }

    /**
     * Instantiates a new Linear timer view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    init {
        val typedArray: TypedArray = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.LinearTimerView, 0, 0
        )

        // Retrieve the view attributes.
        try {
            circleRadiusInDp =
                typedArray.getDimension(R.styleable.LinearTimerView_radius, 80f).toInt()
            strokeWidthInDp =
                typedArray.getDimension(R.styleable.LinearTimerView_strokeWidth, 4f).toInt()
            initialColor = typedArray.getColor(
                R.styleable.LinearTimerView_initialColor,
                ContextCompat.getColor(getContext(), R.color.colorPrimary)
            )
            progressColor = typedArray.getColor(
                R.styleable.LinearTimerView_progressColor,
                ContextCompat.getColor(getContext(), R.color.clr_99000000)
            )
            startingPoint = typedArray.getInt(R.styleable.LinearTimerView_startingPoint, 270)
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            typedArray.recycle()
        }
        init()
    }
}