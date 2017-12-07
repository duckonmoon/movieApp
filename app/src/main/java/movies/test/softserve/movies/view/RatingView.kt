package movies.test.softserve.movies.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import movies.test.softserve.movies.R


/**
 * Created by rkrit on 05.12.17.
 */
class RatingView : View {

    var circleColor: Int = Color.BLUE
        set(value) {
            field = value
            invalidate()
        }

    var level: Int = 1
        set(value) {
            field = value
            invalidate()
        }

    private lateinit var paint: Paint
    private lateinit var textPaint: TextPaint

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.RatingView,
                0, 0
        )
        try {
            circleColor = a.getColor(R.styleable.RatingView_circlecolor, Color.BLUE)
            level = a.getInt(R.styleable.RatingView_lvl, 1)
        } finally {
            a.recycle()
        }
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.RatingView,
                0, 0
        )
        try {
            circleColor = a.getColor(R.styleable.RatingView_circlecolor, Color.BLUE)
            level = a.getInt(R.styleable.RatingView_lvl, 1)
        } finally {
            a.recycle()
        }
        init()
    }

    private fun init() {
        paint = Paint()
        paint.isAntiAlias = true
        textPaint = TextPaint()
        textPaint.isAntiAlias = true
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val desiredWidth = 50
        val desiredHeight = 50

        val minWidth = 20
        val minHeight = 20

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        var width: Int
        var height: Int

        width = when (widthMode) {
            View.MeasureSpec.EXACTLY -> widthSize
            View.MeasureSpec.AT_MOST -> Math.min(desiredWidth, widthSize)
            View.MeasureSpec.UNSPECIFIED -> minWidth
            else -> desiredWidth
        }

        height = when (heightMode) {
            View.MeasureSpec.EXACTLY ->
                heightSize
            View.MeasureSpec.AT_MOST ->
                Math.min(desiredHeight, heightSize)
            View.MeasureSpec.UNSPECIFIED -> minHeight
            else ->
                desiredHeight
        }

        height = Math.min(width, height)
        width = height

        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width
        val h = height

        val pl = paddingLeft
        val pr = paddingRight
        val pt = paddingTop
        val pb = paddingBottom

        val usableWidth = w - (pl + pr)
        val usableHeight = h - (pt + pb)

        val radius = Math.min(usableWidth, usableHeight) / 2
        val cx = pl + usableWidth / 2
        val cy = pt + usableHeight / 2

        paint.color = circleColor
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), paint)
        textPaint.textSize = (width / 2).toFloat()
        textPaint.color = Color.BLACK
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.create("Courier 10 Pitch", Typeface.NORMAL)
        canvas.drawText(level.toString(), cx.toFloat(), cy.toFloat() / 0.73f, textPaint)
    }

}