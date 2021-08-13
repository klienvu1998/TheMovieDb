package com.hyvu.themoviedb.ui

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.OverScroller
import android.widget.Scroller
import androidx.appcompat.widget.AppCompatImageView

class ZoomImageView: AppCompatImageView {
    companion object {
        val TAG = ZoomImageView::class.java.simpleName

        const val ZOOM_TIME = 500f

        // Determine how much the image can be zoom in or out of boundaries, before animating back to min/max boundary.
        const val SUPER_MIN_MULTIPLIER = .75f
        const val SUPER_MAX_MULTIPLIER = 1.25f

        // If setMinZoom(AUTOMATIC_MIN_ZOOM) => set min scale to include the whole image
        const val AUTOMATIC_MIN_ZOOM = -1.0f
    }

    // Scale image from min to max. Min == 1
    private var normalizedScale = 1f

    // Matrix applied to image. Scale X and Y should always be equal. prevMatrix is the matrix saved prior to the screen rotating
    private var currentMatrix: Matrix? = null
    private var prevMatrix: Matrix? = null
    private var zoomEnabled = false

    enum class FixedPixel {CENTER, TOP_LEFT, BOTTOM_RIGHT}

    private var orientationChangeFixedPixel = FixedPixel.CENTER
    private var viewSizeChangeFixedPixel = FixedPixel.CENTER

    private enum class State {NONE, DRAG, ZOOM, FLING, ANIMATE_ZOOM}

    private var state = State.NONE

    private var userSpecifiedMinScale = 0f
    private var minScale = 0f
    private var maxScaleIsSetByMultiplier = false
    private var maxScaleMultiplier = 0f
    private var maxScale = 0f
    private var superMinScale = 0f
    private var superMaxScale = 0f
    private lateinit var m: FloatArray

    private var fling: Fling? = null
    private var orientation: Int = 0

    private var mScaleType: ScaleType? = null

    private var imageRenderedAtLeastOnce = false
    private var onDrawReady = false

    private var delayedZoomVariables: ZoomVariables? = null

    // Size of view and previous view size (ie before rotation)
    private var viewWidth = 0f
    private var viewHeight = 0f
    private var prevViewWidth = 0f
    private var prevViewHeight = 0f

    // Size of image when it is stretched to fit view. Before and After rotation
    private var matchViewWidth = 0f
    private var matchViewHeight = 0f
    private var prevMatchViewWidth = 0f
    private var prevMatchViewHeight = 0f

    private var mScaleDetector: ScaleGestureDetector? = null
    private var mGestureDetector: GestureDetector? = null
    private var doubleTapListener: GestureDetector.OnDoubleTapListener? = null
    private var userTouchListener: OnTouchListener? = null
    private var touchImageViewListener: OnTouchImageViewListener? = null

    private var orientationJustChanged = false
    private var mContext: Context? = null

    interface OnTouchImageViewListener {
        fun onMove()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (attrs != null) {
            configureImageView(context, attrs, defStyleAttr)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configureImageView(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        super.setClickable(true)
        mContext = context
        orientation = resources.configuration.orientation
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mGestureDetector = GestureDetector(context, GestureListener())
        currentMatrix = Matrix()
        prevMatrix = Matrix()
        m = FloatArray(9)
        normalizedScale = 1f
        if (mScaleType == null) {
            mScaleType = ScaleType.FIT_CENTER
        }
        minScale = 1f
        maxScale = 3f
        superMinScale = SUPER_MIN_MULTIPLIER * minScale
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale
        imageMatrix = currentMatrix
        mScaleType = ScaleType.MATRIX
        setState(State.NONE)
        onDrawReady = false
        super.setOnTouchListener(PrivateOnTouchListener())

//        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TouchImageView, defStyleAttr, 0);
//        try {
//            if (attributes != null && !isInEditMode()) {
//                setZoomEnabled(attributes.getBoolean(R.styleable.TouchImageView_zoom_enabled, true));
//            }
//        } finally {
//            // release the TypedArray so that it can be reused.
//            if (attributes != null) {
//                attributes.recycle();
//            }
//        }
    }

    override fun setOnTouchListener(l: OnTouchListener?) {
        userTouchListener = l
    }

    fun setOnTouchImageViewListener(l: OnTouchImageViewListener) {
        touchImageViewListener = l
    }

    fun setOnDoubleTapListener(l: GestureDetector.OnDoubleTapListener) {
        doubleTapListener = l
    }

    fun isZoomEnabled(): Boolean {
        return zoomEnabled
    }

    fun setZoomEnabled(zoomEnabled: Boolean) {
        this.zoomEnabled = zoomEnabled
    }

    override fun setImageResource(resId: Int) {
        imageRenderedAtLeastOnce = false
        super.setImageResource(resId)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        imageRenderedAtLeastOnce = false
        super.setImageDrawable(drawable)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        imageRenderedAtLeastOnce = false
        super.setImageBitmap(bm)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setImageURI(uri: Uri?) {
        imageRenderedAtLeastOnce = false
        super.setImageURI(uri)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setScaleType(type: ScaleType) {
        if (type == ScaleType.MATRIX) {
            super.setScaleType(ScaleType.MATRIX)
        } else {
            mScaleType = type
            if (onDrawReady) {
                //
                // If the image is already rendered, scaleType has been called programmatically
                // and the FrameZoomImageView should be updated with the new scaleType.
                //
                setZoom(this)
            }
        }
    }

    override fun getScaleType(): ScaleType? {
        return mScaleType
    }

    fun getOrientationChangeFixedPixel(): FixedPixel {
        return orientationChangeFixedPixel
    }

    fun setOrientationChangeFixedPixel(fixedPixel: FixedPixel?) {
        orientationChangeFixedPixel = fixedPixel!!
    }

    fun getViewSizeChangeFixedPixel(): FixedPixel {
        return viewSizeChangeFixedPixel
    }

    fun setViewSizeChangeFixedPixel(viewSizeChangeFixedPixel: FixedPixel?) {
        this.viewSizeChangeFixedPixel = viewSizeChangeFixedPixel!!
    }

    fun getM(): Matrix? {
        return currentMatrix
    }

    /**
     * Returns false if image is in initial, unzoomed state. False, otherwise.
     *
     * @return true if image is zoomed
     */
    fun isZoomed(): Boolean {
        return normalizedScale != 1f
    }

    /**
     * Return a Rect representing the zoomed image.
     *
     * @return rect representing zoomed image
     */
    fun getZoomedRect(): RectF? {
        if (mScaleType == ScaleType.FIT_XY) {
            throw UnsupportedOperationException("getZoomedRect() not supported with FIT_XY")
        }
        val topLeft: PointF? = transformCoordTouchToBitmap(0f, 0f, true)
        val bottomRight: PointF? = transformCoordTouchToBitmap(viewWidth, viewHeight, true)
        val w = drawable.intrinsicWidth.toFloat()
        val h = drawable.intrinsicHeight.toFloat()
        if (topLeft != null && bottomRight != null) {
            return RectF(topLeft.x / w, topLeft.y / h, bottomRight.x / w, bottomRight.y / h)
        }
        return null
    }

    /**
     * Save the current matrix and view dimensions
     * in the prevMatrix and prevView variables.
     */
    fun savePreviousImageValues() {
        if (currentMatrix != null && viewHeight != 0f && viewWidth != 0f) {
            currentMatrix!!.getValues(m)
            prevMatrix!!.setValues(m)
            prevMatchViewHeight = matchViewHeight
            prevMatchViewWidth = matchViewWidth
            prevViewHeight = viewHeight
            prevViewWidth = viewWidth
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putInt("orientation", orientation)
        bundle.putFloat("saveScale", normalizedScale)
        bundle.putFloat("matchViewHeight", matchViewHeight)
        bundle.putFloat("matchViewWidth", matchViewWidth)
        bundle.putInt("viewWidth", viewWidth.toInt())
        bundle.putInt("viewHeight", viewHeight.toInt())
        currentMatrix?.getValues(m)
        bundle.putFloatArray("matrix", m)
        bundle.putBoolean("imageRendered", imageRenderedAtLeastOnce)
        bundle.putSerializable("viewSizeChangeFixedPixel", viewSizeChangeFixedPixel)
        bundle.putSerializable("orientationChangeFixedPixel", orientationChangeFixedPixel)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val bundle = state
            normalizedScale = bundle.getFloat("saveScale")
            m = bundle.getFloatArray("matrix")!!
            prevMatrix!!.setValues(m)
            prevMatchViewHeight = bundle.getFloat("matchViewHeight")
            prevMatchViewWidth = bundle.getFloat("matchViewWidth")
            prevViewHeight = bundle.getInt("viewHeight").toFloat()
            prevViewWidth = bundle.getInt("viewWidth").toFloat()
            imageRenderedAtLeastOnce = bundle.getBoolean("imageRendered")
            viewSizeChangeFixedPixel = (bundle.getSerializable("viewSizeChangeFixedPixel") as FixedPixel?)!!
            orientationChangeFixedPixel = (bundle.getSerializable("orientationChangeFixedPixel") as FixedPixel?)!!
            val oldOrientation = bundle.getInt("orientation")
            if (orientation != oldOrientation) {
                orientationJustChanged = true
            }
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"))
            return
        }
        super.onRestoreInstanceState(state)
    }

    override fun onDraw(canvas: Canvas?) {
        onDrawReady = true
        imageRenderedAtLeastOnce = true
        if (delayedZoomVariables != null) {
            setZoom(
                delayedZoomVariables!!.scale,
                delayedZoomVariables!!.focusX,
                delayedZoomVariables!!.focusY,
                delayedZoomVariables!!.scaleType
            )
            delayedZoomVariables = null
        }
        super.onDraw(canvas)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        val newOrientation = resources.configuration.orientation
        if (newOrientation != orientation) {
            orientationJustChanged = true
            orientation = newOrientation
        }
        savePreviousImageValues()
    }

    /**
     * Get the max zoom multiplier.
     *
     * @return max zoom multiplier.
     */
    fun getMaxZoom(): Float {
        return maxScale
    }

    /**
     * Set the max zoom multiplier to a constant. Default value: 3.
     *
     * @param max max zoom multiplier.
     */
    fun setMaxZoom(max: Float) {
        maxScale = max
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale
        maxScaleIsSetByMultiplier = false
    }

    /**
     * Set the max zoom multiplier as a multiple of minZoom, whatever minZoom may change to. By
     * default, this is not done, and maxZoom has a fixed value of 3.
     *
     * @param max max zoom multiplier, as a multiple of minZoom
     */
    fun setMaxZoomRatio(max: Float) {
        maxScaleMultiplier = max
        maxScale = minScale * maxScaleMultiplier
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale
        maxScaleIsSetByMultiplier = true
    }

    /**
     * Get the min zoom multiplier.
     *
     * @return min zoom multiplier.
     */
    fun getMinZoom(): Float {
        return minScale
    }

    /**
     * Get the current zoom. This is the zoom relative to the initial
     * scale, not the original resource.
     *
     * @return current zoom multiplier.
     */
    fun getCurrentZoom(): Float {
        return normalizedScale
    }

    /**
     * Set the min zoom multiplier. Default value: 1.
     *
     * @param min min zoom multiplier.
     */
    fun setMinZoom(min: Float) {
        userSpecifiedMinScale = min
        if (min == AUTOMATIC_MIN_ZOOM) {
            if (mScaleType == ScaleType.CENTER || mScaleType == ScaleType.CENTER_CROP) {
                val drawable = drawable
                val drawableWidth = drawable!!.intrinsicWidth
                val drawableHeight = drawable.intrinsicHeight
                if (drawableWidth > 0 && drawableHeight > 0) {
                    val widthRatio = viewWidth / drawableWidth
                    val heightRatio = viewHeight / drawableHeight
                    minScale = if (mScaleType == ScaleType.CENTER) {
                        Math.min(widthRatio, heightRatio)
                    } else {  // CENTER_CROP
                        Math.min(widthRatio, heightRatio) / Math.max(widthRatio, heightRatio)
                    }
                }
            } else {
                minScale = 1.0f
            }
        } else {
            minScale = userSpecifiedMinScale
        }
        if (maxScaleIsSetByMultiplier) {
            setMaxZoomRatio(maxScaleMultiplier)
        }
        superMinScale = SUPER_MIN_MULTIPLIER * minScale
    }

    /**
     * Reset zoom and translation to initial state.
     */
    fun resetZoom() {
        normalizedScale = 1f
        fitImageToView()
    }

    /**
     * Set zoom to the specified scale. Image will be centered by default.
     *
     * @param scale
     */
    fun setZoom(scale: Float) {
        setZoom(scale, 0.5f, 0.5f)
    }

    /**
     * Set zoom to the specified scale. Image will be centered around the point
     * (focusX, focusY). These floats range from 0 to 1 and denote the focus point
     * as a fraction from the left and top of the view. For example, the top left
     * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
     *
     * @param scale
     * @param focusX
     * @param focusY
     */
    fun setZoom(scale: Float, focusX: Float, focusY: Float) {
        setZoom(scale, focusX, focusY, mScaleType!!)
    }

    /**
     * Set zoom to the specified scale. Image will be centered around the point
     * (focusX, focusY). These floats range from 0 to 1 and denote the focus point
     * as a fraction from the left and top of the view. For example, the top left
     * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
     *
     * @param scale
     * @param focusX
     * @param focusY
     * @param scaleType
     */
    fun setZoom(scale: Float, focusX: Float, focusY: Float, scaleType: ScaleType) {
        //
        // setZoom can be called before the image is on the screen, but at this point,
        // image and view sizes have not yet been calculated in onMeasure. Thus, we should
        // delay calling setZoom until the view has been measured.
        //
        if (!onDrawReady) {
            delayedZoomVariables = ZoomVariables(scale, focusX, focusY, scaleType)
            return
        }
        if (userSpecifiedMinScale == AUTOMATIC_MIN_ZOOM) {
            setMinZoom(AUTOMATIC_MIN_ZOOM)
            if (normalizedScale < minScale) {
                normalizedScale = minScale
            }
        }
        if (scaleType != mScaleType) {
            setScaleType(scaleType)
        }
        resetZoom()
        scaleImage(scale.toDouble(), viewWidth / 2, viewHeight / 2, true)
        currentMatrix?.getValues(m)
        m[Matrix.MTRANS_X] = -(focusX * getImageWidth() - viewWidth * 0.5f)
        m[Matrix.MTRANS_Y] = -(focusY * getImageHeight() - viewHeight * 0.5f)
        currentMatrix?.setValues(m)
        fixTrans()
        imageMatrix = currentMatrix
    }

    /**
     * Set zoom parameters equal to another FrameZoomImageView. Including scale, position,
     * and ScaleType.
     *
     * @param img
     */
    fun setZoom(img: ZoomImageView) {
        val center: PointF? = img.getScrollPosition()
        if (center != null) {
            img.getScaleType()?.let { setZoom(img.getCurrentZoom(), center.x, center.y, it) }
        }
    }

    /**
     * Return the point at the center of the zoomed image. The PointF coordinates range
     * in value between 0 and 1 and the focus point is denoted as a fraction from the left
     * and top of the view. For example, the top left corner of the image would be (0, 0).
     * And the bottom right corner would be (1, 1).
     *
     * @return PointF representing the scroll position of the zoomed image.
     */
    fun getScrollPosition(): PointF? {
        val drawable = drawable ?: return null
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        val point: PointF? = transformCoordTouchToBitmap(viewWidth / 2, viewHeight / 2, true)
        if (point != null) {
            point.x /= drawableWidth.toFloat()
            point.y /= drawableHeight.toFloat()
            return point
        }
        return null
    }

    /**
     * Set the focus point of the zoomed image. The focus points are denoted as a fraction from the
     * left and top of the view. The focus points can range in value between 0 and 1.
     *
     * @param focusX
     * @param focusY
     */
    fun setScrollPosition(focusX: Float, focusY: Float) {
        setZoom(normalizedScale, focusX, focusY)
    }

    /**
     * Performs boundary checking and fixes the image matrix if it
     * is out of bounds.
     */
    private fun fixTrans() {
        currentMatrix?.getValues(m)
        val transX = m[Matrix.MTRANS_X]
        val transY = m[Matrix.MTRANS_Y]
        val fixTransX = getFixTrans(transX, viewWidth, getImageWidth())
        val fixTransY = getFixTrans(transY, viewHeight, getImageHeight())

        if (fixTransX != 0f || fixTransY != 0f) {
            currentMatrix?.postTranslate(fixTransX, fixTransY)
        }
    }

    /**
     * When transitioning from zooming from focus to zoom from center (or vice versa)
     * the image can become unaligned within the view. This is apparent when zooming
     * quickly. When the content size is less than the view size, the content will often
     * be centered incorrectly within the view. fixScaleTrans first calls fixTrans() and
     * then makes sure the image is centered correctly within the view.
     */
    private fun fixScaleTrans() {
        fixTrans()
        currentMatrix?.getValues(m)
        if (getImageWidth() < viewWidth) {
            m[Matrix.MTRANS_X] = (viewWidth - getImageWidth()) / 2
        }

        if (getImageHeight() < viewHeight) {
            m[Matrix.MTRANS_Y] = (viewHeight - getImageHeight()) / 2
        }
        currentMatrix?.setValues(m)
    }

    private fun getFixTrans(trans: Float, viewSize: Float, contentSize: Float): Float {
        var minTrans = 0f
        var maxTrans = 0f
        if (contentSize <= viewSize) {
            minTrans = 0f
            maxTrans = viewSize - contentSize
        } else {
            minTrans = viewSize - contentSize
            maxTrans = 0f
        }

        if (trans < minTrans) return -trans + minTrans
        if (trans > maxTrans) return -trans + maxTrans
        return 0f
    }

    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        return if (contentSize <= viewSize) {
            0f
        } else delta
    }

    private fun getImageWidth(): Float {
        return matchViewWidth * normalizedScale
    }

    private fun getImageHeight(): Float {
        return matchViewHeight * normalizedScale
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val drawable = drawable
        val drawableWidth = drawable?.intrinsicWidth ?: 0
        val drawableHeight = drawable?.intrinsicHeight ?: 0
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val totalViewWidth: Int = setViewSize(widthMode, widthSize, drawableWidth)
        val totalViewHeight: Int = setViewSize(heightMode, heightSize, drawableHeight)
        if (!orientationJustChanged) {
            savePreviousImageValues()
        }

        // Image view width, height must consider padding
        val width = totalViewWidth - paddingLeft - paddingRight
        val height = totalViewHeight - paddingTop - paddingBottom

        //
        // Set view dimensions
        //
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //
        // Fit content within view.
        //
        // onMeasure may be called multiple times for each layout change, including orientation
        // changes. For example, if the FrameZoomImageView is inside a ConstraintLayout, onMeasure may
        // be called with:
        // widthMeasureSpec == "AT_MOST 2556" and then immediately with
        // widthMeasureSpec == "EXACTLY 1404", then back and forth multiple times in quick
        // succession, as the ConstraintLayout tries to solve its constraints.
        //
        // onSizeChanged is called once after the final onMeasure is called. So we make all changes
        // to class members, such as fitting the image into the new shape of the FrameZoomImageView,
        // here, after the final size has been determined. This helps us avoid both
        // repeated computations, and making irreversible changes (e.g. making the View temporarily too
        // big or too small, thus making the current zoom fall outside of an automatically-changing
        // minZoom and maxZoom).
        //
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
        fitImageToView()
    }

    /**
     * This function can be called:
     * 1. When the FrameZoomImageView is first loaded (onMeasure).
     * 2. When a new image is loaded (setImageResource|Bitmap|Drawable|URI).
     * 3. On rotation (onSaveInstanceState, then onRestoreInstanceState, then onMeasure).
     * 4. When the view is resized (onMeasure).
     * 5. When the zoom is reset (resetZoom).
     *
     *
     * In cases 2, 3 and 4, we try to maintain the zoom state and position as directed by
     * orientationChangeFixedPixel or viewSizeChangeFixedPixel (if there is an existing zoom state
     * and position, which there might not be in case 2).
     *
     *
     * If the normalizedScale is equal to 1, then the image is made to fit the View. Otherwise, we
     * maintain zoom level and attempt to roughly put the same part of the image in the View as was
     * there before, paying attention to orientationChangeFixedPixel or viewSizeChangeFixedPixel.
     */
    private fun fitImageToView() {
        val fixedPixel = if (orientationJustChanged) orientationChangeFixedPixel else viewSizeChangeFixedPixel
        orientationJustChanged = false
        val drawable = drawable
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) {
            return
        }
        if (currentMatrix == null || prevMatrix == null) {
            return
        }
        if (userSpecifiedMinScale == AUTOMATIC_MIN_ZOOM) {
            setMinZoom(AUTOMATIC_MIN_ZOOM)
            if (normalizedScale < minScale) {
                normalizedScale = minScale
            }
        }
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight

        //
        // Scale image for view
        //
        var scaleX = viewWidth / drawableWidth
        var scaleY = viewHeight / drawableHeight
        when (mScaleType) {
            ScaleType.CENTER -> {
                scaleY = 1f
                scaleX = scaleY
            }
            ScaleType.CENTER_CROP -> {
                scaleY = Math.max(scaleX, scaleY)
                scaleX = scaleY
            }
            ScaleType.CENTER_INSIDE -> {
                run {
                    scaleY = Math.min(1f, Math.min(scaleX, scaleY))
                    scaleX = scaleY
                }
                run {
                    scaleY = Math.min(scaleX, scaleY)
                    scaleX = scaleY
                }
            }
            ScaleType.FIT_CENTER, ScaleType.FIT_START, ScaleType.FIT_END -> {
                scaleY = Math.min(scaleX, scaleY)
                scaleX = scaleY
            }
            ScaleType.FIT_XY -> {
            }
            else -> {
            }
        }

        //
        // Put the image's center in the right place.
        //
        val redundantXSpace = viewWidth - scaleX * drawableWidth
        val redundantYSpace = viewHeight - scaleY * drawableHeight
        matchViewWidth = viewWidth - redundantXSpace
        matchViewHeight = viewHeight - redundantYSpace
        if (!isZoomed() && !imageRenderedAtLeastOnce) {
            //
            // Stretch and center image to fit view
            //
            currentMatrix?.setScale(scaleX, scaleY)
            when (mScaleType) {
                ScaleType.FIT_START -> currentMatrix?.postTranslate(0f, 0f)
                ScaleType.FIT_END -> currentMatrix?.postTranslate(redundantXSpace, redundantYSpace)
                else -> currentMatrix?.postTranslate(redundantXSpace / 2, redundantYSpace / 2)
            }
            normalizedScale = 1f
        } else {
            //
            // These values should never be 0 or we will set viewWidth and viewHeight
            // to NaN in newTranslationAfterChange. To avoid this, call savePreviousImageValues
            // to set them equal to the current values.
            //
            if (prevMatchViewWidth == 0f || prevMatchViewHeight == 0f) {
                savePreviousImageValues()
            }

            //
            // Use the previous matrix as our starting point for the new matrix.
            //
            prevMatrix!!.getValues(m)

            //
            // Rescale Matrix if appropriate
            //
            m[Matrix.MSCALE_X] = matchViewWidth / drawableWidth * normalizedScale
            m[Matrix.MSCALE_Y] = matchViewHeight / drawableHeight * normalizedScale

            //
            // TransX and TransY from previous matrix
            //
            val transX = m[Matrix.MTRANS_X]
            val transY = m[Matrix.MTRANS_Y]

            //
            // X position
            //
            val prevActualWidth = prevMatchViewWidth * normalizedScale
            val actualWidth = getImageWidth()
            m[Matrix.MTRANS_X] = newTranslationAfterChange(
                transX,
                prevActualWidth,
                actualWidth,
                prevViewWidth,
                viewWidth,
                drawableWidth,
                fixedPixel
            )

            //
            // Y position
            //
            val prevActualHeight = prevMatchViewHeight * normalizedScale
            val actualHeight = getImageHeight()
            m[Matrix.MTRANS_Y] = newTranslationAfterChange(
                transY,
                prevActualHeight,
                actualHeight,
                prevViewHeight,
                viewHeight,
                drawableHeight,
                fixedPixel
            )

            //
            // Set the matrix to the adjusted scale and translation values.
            //
            currentMatrix?.setValues(m)
        }
        fixTrans()
        imageMatrix = currentMatrix
    }

    /**
     * Set view dimensions based on layout params
     *
     * @param mode
     * @param size
     * @param drawableWidth
     * @return
     */
    private fun setViewSize(mode: Int, size: Int, drawableWidth: Int): Int {
        val viewSize: Int
        viewSize = when (mode) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST -> Math.min(drawableWidth, size)
            MeasureSpec.UNSPECIFIED -> drawableWidth
            else -> size
        }
        return viewSize
    }

    /**
     * After any change described in the comments for fitImageToView, the matrix needs to be
     * translated. This function translates the image so that the fixed pixel in the image
     * stays in the same place in the View.
     *
     * @param trans                the value of trans in that axis before the rotation
     * @param prevImageSize        the width/height of the image before the rotation
     * @param imageSize            width/height of the image after rotation
     * @param prevViewSize         width/height of view before rotation
     * @param viewSize             width/height of view after rotation
     * @param drawableSize         width/height of drawable
     * @param sizeChangeFixedPixel how we should choose the fixed pixel
     */
    private fun newTranslationAfterChange(
        trans: Float,
        prevImageSize: Float,
        imageSize: Float,
        prevViewSize: Float,
        viewSize: Float,
        drawableSize: Int,
        sizeChangeFixedPixel: FixedPixel
    ): Float {
        return if (imageSize < viewSize) {
            //
            // The width/height of image is less than the view's width/height. Center it.
            //
            (viewSize - drawableSize * m[Matrix.MSCALE_X]) * 0.5f
        } else if (trans > 0) {
            //
            // The image is larger than the view, but was not before the view changed. Center it.
            //
            -((imageSize - viewSize) * 0.5f)
        } else {
            //
            // Where is the pixel in the View that we are keeping stable, as a fraction of the
            // width/height of the View?
            //
            var fixedPixelPositionInView = 0.5f // CENTER
            if (sizeChangeFixedPixel == FixedPixel.BOTTOM_RIGHT) {
                fixedPixelPositionInView = 1.0f
            } else if (sizeChangeFixedPixel == FixedPixel.TOP_LEFT) {
                fixedPixelPositionInView = 0.0f
            }
            //
            // Where is the pixel in the Image that we are keeping stable, as a fraction of the
            // width/height of the Image?
            //
            val fixedPixelPositionInImage = (-trans + fixedPixelPositionInView * prevViewSize) / prevImageSize
            //
            // Here's what the new translation should be so that, after whatever change triggered
            // this function to be called, the pixel at fixedPixelPositionInView of the View is
            // still the pixel at fixedPixelPositionInImage of the image.
            //
            -(fixedPixelPositionInImage * imageSize - viewSize * fixedPixelPositionInView)
        }
    }

    private fun setState(state: State) {
        this.state = state
    }

    fun canScrollHorizontallyFroyo(direction: Int): Boolean {
        return canScrollHorizontally(direction)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        currentMatrix?.getValues(m)
        val x = m[Matrix.MTRANS_X]
        if (getImageWidth() < viewWidth) {
            return false
        } else if (x >= -1 && direction < 0) {
            return false
        } else if (Math.abs(x) + viewWidth + 1 >= getImageWidth() && direction > 0) {
            return false
        }
        return true
    }

    override fun canScrollVertically(direction: Int): Boolean {
        currentMatrix?.getValues(m)
        val y = m[Matrix.MTRANS_Y]
        if (getImageHeight() < viewHeight) {
            return false
        } else if (y >= -1 && direction < 0) {
            return false
        } else if (Math.abs(y) + viewHeight + 1 >= getImageHeight() && direction > 0) {
            return false
        }
        return true
    }

    /**
     * Gesture Listener detects a single click or long click and passes that on
     * to the view's listener.
     *
     * @author Ortiz
     */
    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return if (doubleTapListener != null) {
                doubleTapListener!!.onSingleTapConfirmed(e)
            } else performClick()
        }

        override fun onLongPress(e: MotionEvent) {
            performLongClick()
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (fling != null) {
                //
                // If a previous fling is still active, it should be cancelled so that two flings
                // are not run simultaenously.
                //
                fling?.cancelFling()
            }
            fling = Fling(velocityX.toInt(), velocityY.toInt())
            compatPostOnAnimation(fling!!)
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            var consumed = false
            if (isZoomEnabled()) {
                if (doubleTapListener != null) {
                    consumed = doubleTapListener!!.onDoubleTap(e)
                }
                if (state == State.NONE) {
                    val targetZoom: Float = if (normalizedScale == minScale) maxScale else minScale
                    val doubleTap: DoubleTapZoom = DoubleTapZoom(targetZoom, e.x, e.y, false)
                    compatPostOnAnimation(doubleTap)
                    consumed = true
                }
            }
            return consumed
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            return if (doubleTapListener != null) {
                doubleTapListener!!.onDoubleTapEvent(e)
            } else false
        }
    }

    /**
     * Responsible for all touch events. Handles the heavy lifting of drag and also sends
     * touch events to Scale Detector and Gesture Detector.
     *
     * @author Ortiz
     */
    private inner class PrivateOnTouchListener : OnTouchListener {
        //
        // Remember last point position for dragging
        //
        private val last = PointF()
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (getDrawable() == null) {
                setState(State.NONE)
                return false
            }
            mScaleDetector?.onTouchEvent(event)
            mGestureDetector?.onTouchEvent(event)
            val curr = PointF(event.x, event.y)
            if (state == State.NONE || state == State.DRAG || state == State.FLING) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        last.set(curr)
                        if (fling != null) fling?.cancelFling()
                        setState(State.DRAG)
                    }
                    MotionEvent.ACTION_MOVE -> if (state == State.DRAG) {
                        val deltaX = curr.x - last.x
                        val deltaY = curr.y - last.y
                        val fixTransX: Float = getFixDragTrans(deltaX, viewWidth, getImageWidth())
                        val fixTransY: Float = getFixDragTrans(deltaY, viewHeight, getImageHeight())
                        currentMatrix?.postTranslate(fixTransX, fixTransY)
                        fixTrans()
                        last[curr.x] = curr.y
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> setState(State.NONE)
                }
            }
            setImageMatrix(currentMatrix)

            //
            // User-defined OnTouchListener
            //
            if (userTouchListener != null) {
                userTouchListener?.onTouch(v, event)
            }

            //
            // OnTouchImageViewListener is set: FrameZoomImageView dragged by user.
            //
            if (touchImageViewListener != null) {
                touchImageViewListener?.onMove()
            }

            //
            // indicate event was handled
            //
            return true
        }
    }

    /**
     * ScaleListener detects user two finger scaling and scales image.
     *
     * @author Ortiz
     */
    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            setState(State.ZOOM)
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleImage(detector.scaleFactor.toDouble(), detector.focusX, detector.focusY, true)

            //
            // OnTouchImageViewListener is set: FrameZoomImageView pinch zoomed by user.
            //
            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            super.onScaleEnd(detector)
            setState(State.NONE)
            var animateToZoomBoundary = false
            var targetZoom: Float = normalizedScale
            if (normalizedScale > maxScale) {
                targetZoom = maxScale
                animateToZoomBoundary = true
            } else if (normalizedScale < minScale) {
                targetZoom = minScale
                animateToZoomBoundary = true
            }
            if (animateToZoomBoundary) {
                val doubleTap = DoubleTapZoom(targetZoom, viewWidth / 2, viewHeight / 2, true)
                compatPostOnAnimation(doubleTap)
            }
        }
    }

    private fun scaleImage(
        deltaScale: Double,
        focusX: Float,
        focusY: Float,
        stretchImageToSuper: Boolean
    ) {
        var tDeltaScale = deltaScale
        val lowerScale: Float
        val upperScale: Float
        if (stretchImageToSuper) {
            lowerScale = superMinScale
            upperScale = superMaxScale
        } else {
            lowerScale = minScale
            upperScale = maxScale
        }
        val origScale = normalizedScale
        normalizedScale *= tDeltaScale.toFloat()
        if (normalizedScale > upperScale) {
            normalizedScale = upperScale
            tDeltaScale = (upperScale / origScale).toDouble()
        } else if (normalizedScale < lowerScale) {
            normalizedScale = lowerScale
            tDeltaScale = (lowerScale / origScale).toDouble()
        }
        currentMatrix?.postScale(tDeltaScale.toFloat(), tDeltaScale.toFloat(), focusX, focusY)
        fixScaleTrans()
    }

    /**
     * DoubleTapZoom calls a series of runnables which apply
     * an animated zoom in/out graphic to the image.
     *
     * @author Ortiz
     */
    private inner class DoubleTapZoom internal constructor(
        targetZoom: Float,
        focusX: Float,
        focusY: Float,
        stretchImageToSuper: Boolean
    ) : Runnable {
        private val startTime: Long
        private val startZoom: Float
        private val targetZoom: Float
        private var bitmapX: Float = 0f
        private var bitmapY: Float = 0f
        private val stretchImageToSuper: Boolean
        private val interpolator = AccelerateDecelerateInterpolator()
        private var startTouch: PointF? = null
        private var endTouch: PointF? = null
        override fun run() {
            if (getDrawable() == null) {
                setState(State.NONE)
                return
            }
            val t = interpolate()
            val deltaScale = calculateDeltaScale(t)
            scaleImage(deltaScale, bitmapX, bitmapY, stretchImageToSuper)
            translateImageToCenterTouchPosition(t)
            fixScaleTrans()
            setImageMatrix(currentMatrix)

            //
            // OnTouchImageViewListener is set: double tap runnable updates listener
            // with every frame.
            //
            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }
            if (t < 1f) {
                //
                // We haven't finished zooming
                //
                compatPostOnAnimation(this)
            } else {
                //
                // Finished zooming
                //
                setState(State.NONE)
            }
        }

        /**
         * Interpolate between where the image should start and end in order to translate
         * the image so that the point that is touched is what ends up centered at the end
         * of the zoom.
         *
         * @param t
         */
        private fun translateImageToCenterTouchPosition(t: Float) {
            if (startTouch != null && endTouch != null) {
                val targetX = startTouch!!.x + t * (endTouch!!.x - startTouch!!.x)
                val targetY = startTouch!!.y + t * (endTouch!!.y - startTouch!!.y)
                val curr: PointF? = transformCoordBitmapToTouch(bitmapX, bitmapY)
                if (curr != null) currentMatrix?.postTranslate(targetX - curr.x, targetY - curr.y)
            }
        }

        /**
         * Use interpolator to get t
         *
         * @return
         */
        private fun interpolate(): Float {
            val currTime = System.currentTimeMillis()
            var elapsed = (currTime - startTime) / ZOOM_TIME
            elapsed = Math.min(1f, elapsed)
            return interpolator.getInterpolation(elapsed)
        }

        /**
         * Interpolate the current targeted zoom and get the delta
         * from the current zoom.
         *
         * @param t
         * @return
         */
        private fun calculateDeltaScale(t: Float): Double {
            val zoom = (startZoom + t * (targetZoom - startZoom)).toDouble()
            return zoom / normalizedScale
        }

        init {
            setState(State.ANIMATE_ZOOM)
            startTime = System.currentTimeMillis()
            startZoom = normalizedScale
            this.targetZoom = targetZoom
            this.stretchImageToSuper = stretchImageToSuper
            val bitmapPoint: PointF? = transformCoordTouchToBitmap(focusX, focusY, false)
            if (bitmapPoint != null) {
                bitmapX = bitmapPoint.x
                bitmapY = bitmapPoint.y

                //
                // Used for translating image during scaling
                //
                startTouch = transformCoordBitmapToTouch(bitmapX, bitmapY)
                endTouch = PointF(viewWidth / 2, viewHeight / 2)
            }
        }
    }

    /**
     * This function will transform the coordinates in the touch event to the coordinate
     * system of the drawable that the imageview contain
     *
     * @param x            x-coordinate of touch event
     * @param y            y-coordinate of touch event
     * @param clipToBitmap Touch event may occur within view, but outside image content. True, to clip return value
     * to the bounds of the bitmap size.
     * @return Coordinates of the point touched, in the coordinate system of the original drawable.
     */
    private fun transformCoordTouchToBitmap(x: Float, y: Float, clipToBitmap: Boolean): PointF {
        currentMatrix?.getValues(m)
        val origW = drawable.intrinsicWidth.toFloat()
        val origH = drawable.intrinsicHeight.toFloat()
        val transX = m[Matrix.MTRANS_X]
        val transY = m[Matrix.MTRANS_Y]
        var finalX = (x - transX) * origW / getImageWidth()
        var finalY = (y - transY) * origH / getImageHeight()
        if (clipToBitmap) {
            finalX = Math.min(Math.max(finalX, 0f), origW)
            finalY = Math.min(Math.max(finalY, 0f), origH)
        }
        return PointF(finalX, finalY)
    }

    /**
     * Inverse of transformCoordTouchToBitmap. This function will transform the coordinates in the
     * drawable's coordinate system to the view's coordinate system.
     *
     * @param bx x-coordinate in original bitmap coordinate system
     * @param by y-coordinate in original bitmap coordinate system
     * @return Coordinates of the point in the view's coordinate system.
     */
    private fun transformCoordBitmapToTouch(bx: Float, by: Float): PointF {
        currentMatrix?.getValues(m)
        val origW = drawable.intrinsicWidth.toFloat()
        val origH = drawable.intrinsicHeight.toFloat()
        val px = bx / origW
        val py = by / origH
        val finalX = m[Matrix.MTRANS_X] + getImageWidth() * px
        val finalY = m[Matrix.MTRANS_Y] + getImageHeight() * py
        return PointF(finalX, finalY)
    }

    /**
     * Fling launches sequential runnables which apply
     * the fling graphic to the image. The values for the translation
     * are interpolated by the Scroller.
     *
     * @author Ortiz
     */
    private inner class Fling: Runnable {
        var scroller: CompatScroller?
        var currX: Int
        var currY: Int

        constructor(velocityX: Int, velocityY: Int) {
            setState(State.FLING)
            scroller = CompatScroller(mContext)
            matrix.getValues(m)
            val startX = m[Matrix.MTRANS_X].toInt()
            val startY = m[Matrix.MTRANS_Y].toInt()
            val minX: Int
            val maxX: Int
            val minY: Int
            val maxY: Int
            if (getImageWidth() > viewWidth) {
                minX = (viewWidth - getImageWidth().toInt()).toInt()
                maxX = 0
            } else {
                maxX = startX
                minX = maxX
            }
            if (getImageHeight() > viewHeight) {
                minY = (viewHeight - getImageHeight().toInt()).toInt()
                maxY = 0
            } else {
                maxY = startY
                minY = maxY
            }
            scroller!!.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY)
            currX = startX
            currY = startY
        }

        fun cancelFling() {
            if (scroller != null) {
                setState(State.NONE)
                scroller!!.forceFinished(true)
            }
        }

        override fun run() {

            //
            // OnTouchImageViewListener is set: FrameZoomImageView listener has been flung by user.
            // Listener runnable updated with each frame of fling animation.
            //
            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }
            if (scroller!!.isFinished) {
                scroller = null
                return
            }
            if (scroller!!.computeScrollOffset()) {
                val newX = scroller!!.currX
                val newY = scroller!!.currY
                val transX = newX - currX
                val transY = newY - currY
                currX = newX
                currY = newY
                currentMatrix?.postTranslate(transX.toFloat(), transY.toFloat())
                fixTrans()
                imageMatrix = currentMatrix
                compatPostOnAnimation(this)
            }
        }
    }

    @TargetApi(VERSION_CODES.GINGERBREAD)
    private class CompatScroller constructor(context: Context?) {
        var scroller: Scroller? = null
        var overScroller: OverScroller
        fun fling(
            startX: Int,
            startY: Int,
            velocityX: Int,
            velocityY: Int,
            minX: Int,
            maxX: Int,
            minY: Int,
            maxY: Int
        ) {
            overScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY)
        }

        fun forceFinished(finished: Boolean) {
            overScroller.forceFinished(finished)
        }

        val isFinished: Boolean
            get() = overScroller.isFinished

        fun computeScrollOffset(): Boolean {
            overScroller.computeScrollOffset()
            return overScroller.computeScrollOffset()
        }

        val currX: Int
            get() = overScroller.currX
        val currY: Int
            get() = overScroller.currY

        init {
            overScroller = OverScroller(context)
        }
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    private fun compatPostOnAnimation(runnable: Runnable) {
        postOnAnimation(runnable)
    }

    private class ZoomVariables internal constructor(
        var scale: Float,
        var focusX: Float,
        var focusY: Float,
        var scaleType: ScaleType
    )

    private fun printMatrixInfo() {
        val n = FloatArray(9)
        currentMatrix?.getValues(n)
        Log.d(
            TAG,
            "Scale: " + n[Matrix.MSCALE_X] + " TransX: " + n[Matrix.MTRANS_X] + " TransY: " + n[Matrix.MTRANS_Y]
        )
    }
}