package com.unnamed.b.atv.view


import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Scroller
import android.widget.TextView

/**
 * Layout container for a view hierarchy that can be scrolled by the user,
 * allowing it to be larger than the physical display.  A TwoDScrollView
 * is a [FrameLayout], meaning you should place one child in it
 * containing the entire contents to scroll; this child may itself be a layout
 * manager with a complex hierarchy of objects.  A child that is often used
 * is a [LinearLayout] in a vertical orientation, presenting a vertical
 * array of top-level items that the user can scroll through.
 *
 *
 *
 * The [TextView] class also
 * takes care of its own scrolling, so does not require a TwoDScrollView, but
 * using the two together is possible to achieve the effect of a text view
 * within a larger container.
 * Converted to Kolin by Kumar Shivang on 16/07/17
 */
class TwoDScrollView : FrameLayout {

    private var mLastScroll: Long = 0

    private val mTempRect = Rect()
    private var mScroller: Scroller? = null

    /**
     * Flag to indicate that we are moving focus ourselves. This is so the
     * code that watches for focus changes initiated outside this TwoDScrollView
     * knows that it does not have to do anything.
     */
    private var mTwoDScrollViewMovedFocus: Boolean = false

    /**
     * Position of the last motion event.
     */
    private var mLastMotionY: Float = 0.toFloat()
    private var mLastMotionX: Float = 0.toFloat()

    /**
     * True when the layout has changed but the traversal has not come through yet.
     * Ideally the view hierarchy would keep track of this for us.
     */
    private var mIsLayoutDirty = true

    /**
     * The child to give focus to in the event that a child has requested focus while the
     * layout is dirty. This prevents the scroll from being wrong if the child has not been
     * laid out before requesting focus.
     */
    private var mChildToScrollTo: View? = null

    /**
     * True if the user is currently dragging this TwoDScrollView around. This is
     * not the same as 'is being flinged', which can be checked by
     * mScroller.isFinished() (flinging begins when the user lifts his finger).
     */
    private var mIsBeingDragged = false

    /**
     * Determines speed during touch scrolling
     */
    private var mVelocityTracker: VelocityTracker? = null

    /**
     * Whether arrow scrolling is animated.
     */
    private var mTouchSlop: Int = 0
    private var mMinimumVelocity: Int = 0
    private var mMaximumVelocity: Int = 0

    constructor(context: Context) : super(context) {
        initTwoDScrollView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initTwoDScrollView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initTwoDScrollView()
    }

    override fun getTopFadingEdgeStrength(): Float {
        if (childCount == 0) {
            return 0.0f
        }
        val length = verticalFadingEdgeLength
        if (scrollY < length) {
            return scrollY / length.toFloat()
        }
        return 1.0f
    }

    override fun getBottomFadingEdgeStrength(): Float {
        if (childCount == 0) {
            return 0.0f
        }
        val length = verticalFadingEdgeLength
        val bottomEdge = height - paddingBottom
        val span = getChildAt(0).bottom - scrollY - bottomEdge
        if (span < length) {
            return span / length.toFloat()
        }
        return 1.0f
    }

    override fun getLeftFadingEdgeStrength(): Float {
        if (childCount == 0) {
            return 0.0f
        }
        val length = horizontalFadingEdgeLength
        if (scrollX < length) {
            return scrollX / length.toFloat()
        }
        return 1.0f
    }

    override fun getRightFadingEdgeStrength(): Float {
        if (childCount == 0) {
            return 0.0f
        }
        val length = horizontalFadingEdgeLength
        val rightEdge = width - paddingRight
        val span = getChildAt(0).right - scrollX - rightEdge
        if (span < length) {
            return span / length.toFloat()
        }
        return 1.0f
    }

    /**
     * @return The maximum amount this scroll view will scroll in response to
     * * an arrow event.
     */
    val maxScrollAmountVertical: Int
        get() = (MAX_SCROLL_FACTOR * height).toInt()

    val maxScrollAmountHorizontal: Int
        get() = (MAX_SCROLL_FACTOR * width).toInt()

    private fun initTwoDScrollView() {
        mScroller = Scroller(context)
        isFocusable = true
        descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        setWillNotDraw(false)
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledTouchSlop
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity
    }

    override fun addView(child: View) {
        if (childCount > 0) {
            throw IllegalStateException("TwoDScrollView can host only one direct child")
        }
        super.addView(child)
    }

    override fun addView(child: View, index: Int) {
        if (childCount > 0) {
            throw IllegalStateException("TwoDScrollView can host only one direct child")
        }
        super.addView(child, index)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        if (childCount > 0) {
            throw IllegalStateException("TwoDScrollView can host only one direct child")
        }
        super.addView(child, params)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (childCount > 0) {
            throw IllegalStateException("TwoDScrollView can host only one direct child")
        }
        super.addView(child, index, params)
    }

    /**
     * @return Returns true this TwoDScrollView can be scrolled
     */
    private fun canScroll(): Boolean {
        val child = getChildAt(0)
        if (child != null) {
            val childHeight = child.height
            val childWidth = child.width
            return height < childHeight + paddingTop + paddingBottom || width < childWidth + paddingLeft + paddingRight
        }
        return false
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Let the focused view and/or our descendants get the key first
        val handled = super.dispatchKeyEvent(event)
        if (handled) {
            return true
        }
        return executeKeyEvent(event)
    }

    /**
     * You can call this function yourself to have the scroll view perform
     * scrolling from a key event, just as if the event had been dispatched to
     * it by the view hierarchy.

     * @param event The key event to execute.
     * *
     * @return Return true if the event was handled, else false.
     */
    fun executeKeyEvent(event: KeyEvent): Boolean {
        mTempRect.setEmpty()
        if (!canScroll()) {
            if (isFocused) {
                var currentFocused: View? = findFocus()
                if (currentFocused === this) currentFocused = null
                val nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, View.FOCUS_DOWN)
                return nextFocused != null && nextFocused !== this && nextFocused.requestFocus(View.FOCUS_DOWN)
            }
            return false
        }
        var handled = false
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> if (!event.isAltPressed) {
                    handled = arrowScroll(View.FOCUS_UP, false)
                } else {
                    handled = fullScroll(View.FOCUS_UP, false)
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> if (!event.isAltPressed) {
                    handled = arrowScroll(View.FOCUS_DOWN, false)
                } else {
                    handled = fullScroll(View.FOCUS_DOWN, false)
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> if (!event.isAltPressed) {
                    handled = arrowScroll(View.FOCUS_LEFT, true)
                } else {
                    handled = fullScroll(View.FOCUS_LEFT, true)
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> if (!event.isAltPressed) {
                    handled = arrowScroll(View.FOCUS_RIGHT, true)
                } else {
                    handled = fullScroll(View.FOCUS_RIGHT, true)
                }
            }
        }
        return handled
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        /*
   * This method JUST determines whether we want to intercept the motion.
   * If we return true, onMotionEvent will be called and we do the actual
   * scrolling there.
   *
   * Shortcut the most recurring case: the user is in the dragging
   * state and he is moving his finger.  We want to intercept this
   * motion.
   */
        val action = ev.action
        if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
            return true
        }
        if (!canScroll()) {
            mIsBeingDragged = false
            return false
        }
        val y = ev.y
        val x = ev.x
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                /*
       * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
       * whether the user has moved far enough from his original down touch.
       */
                /*
       * Locally do absolute value. mLastMotionY is set to the y value
       * of the down event.
       */
                val yDiff = Math.abs(y - mLastMotionY).toInt()
                val xDiff = Math.abs(x - mLastMotionX).toInt()
                if (yDiff > mTouchSlop || xDiff > mTouchSlop) {
                    mIsBeingDragged = true
                }
            }

            MotionEvent.ACTION_DOWN -> {
                /* Remember location of down touch */
                mLastMotionY = y
                mLastMotionX = x

                /*
       * If being flinged and user touches the screen, initiate drag;
       * otherwise don't.  mScroller.isFinished should be false when
       * being flinged.
       */
                mIsBeingDragged = !mScroller!!.isFinished
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP ->
                /* Release the drag */
                mIsBeingDragged = false
        }

        /*
   * The only time we want to intercept motion events is if we are in the
   * drag mode.
   */
        return mIsBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {

        if (ev.action == MotionEvent.ACTION_DOWN && ev.edgeFlags != 0) {
            // Don't handle edge touches immediately -- they may actually belong to one of our
            // descendants.
            return false
        }

        if (!canScroll()) {
            return false
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(ev)

        val action = ev.action
        val y = ev.y
        val x = ev.x

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                /*
       * If being flinged and user touches, stop the fling. isFinished
       * will be false if being flinged.
       */
                if (!mScroller!!.isFinished) {
                    mScroller!!.abortAnimation()
                }

                // Remember where the motion event started
                mLastMotionY = y
                mLastMotionX = x
            }
            MotionEvent.ACTION_MOVE -> {
                // Scroll to follow the motion event
                var deltaX = (mLastMotionX - x).toInt()
                var deltaY = (mLastMotionY - y).toInt()
                mLastMotionX = x
                mLastMotionY = y

                if (deltaX < 0) {
                    if (scrollX < 0) {
                        deltaX = 0
                    }
                } else if (deltaX > 0) {
                    val rightEdge = width - paddingRight
                    val availableToScroll = getChildAt(0).right - scrollX - rightEdge
                    if (availableToScroll > 0) {
                        deltaX = Math.min(availableToScroll, deltaX)
                    } else {
                        deltaX = 0
                    }
                }
                if (deltaY < 0) {
                    if (scrollY < 0) {
                        deltaY = 0
                    }
                } else if (deltaY > 0) {
                    val bottomEdge = height - paddingBottom
                    val availableToScroll = getChildAt(0).bottom - scrollY - bottomEdge
                    if (availableToScroll > 0) {
                        deltaY = Math.min(availableToScroll, deltaY)
                    } else {
                        deltaY = 0
                    }
                }
                if (deltaY != 0 || deltaX != 0)
                    scrollBy(deltaX, deltaY)
            }
            MotionEvent.ACTION_UP -> {
                val velocityTracker = mVelocityTracker
                velocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                val initialXVelocity = velocityTracker.xVelocity.toInt()
                val initialYVelocity = velocityTracker.yVelocity.toInt()
                if (Math.abs(initialXVelocity) + Math.abs(initialYVelocity) > mMinimumVelocity && childCount > 0) {
                    fling(-initialXVelocity, -initialYVelocity)
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
            }
        }
        return true
    }

    /**
     * Finds the next focusable component that fits in this View's bounds
     * (excluding fading edges) pretending that this View's top is located at
     * the parameter top.

     * @param topFocus           look for a candidate is the one at the top of the bounds
     * *                           if topFocus is true, or at the bottom of the bounds if topFocus is
     * *                           false
     * *
     * @param top                the top offset of the bounds in which a focusable must be
     * *                           found (the fading edge is assumed to start at this position)
     * *
     * @param preferredFocusable the View that has highest priority and will be
     * *                           returned if it is within my bounds (null is valid)
     * *
     * @return the next focusable component in the bounds or null if none can be
     * * found
     */
    private fun findFocusableViewInMyBounds(topFocus: Boolean, top: Int, leftFocus: Boolean, left: Int, preferredFocusable: View?): View? {
        /*
   * The fading edge's transparent side should be considered for focus
   * since it's mostly visible, so we divide the actual fading edge length
   * by 2.
   */
        val verticalFadingEdgeLength = verticalFadingEdgeLength / 2
        val topWithoutFadingEdge = top + verticalFadingEdgeLength
        val bottomWithoutFadingEdge = top + height - verticalFadingEdgeLength
        val horizontalFadingEdgeLength = horizontalFadingEdgeLength / 2
        val leftWithoutFadingEdge = left + horizontalFadingEdgeLength
        val rightWithoutFadingEdge = left + width - horizontalFadingEdgeLength

        if (preferredFocusable != null
                && preferredFocusable.top < bottomWithoutFadingEdge
                && preferredFocusable.bottom > topWithoutFadingEdge
                && preferredFocusable.left < rightWithoutFadingEdge
                && preferredFocusable.right > leftWithoutFadingEdge) {
            return preferredFocusable
        }
        return findFocusableViewInBounds(topFocus, topWithoutFadingEdge, bottomWithoutFadingEdge, leftFocus, leftWithoutFadingEdge, rightWithoutFadingEdge)
    }

    /**
     * Finds the next focusable component that fits in the specified bounds.
     *

     * @param topFocus look for a candidate is the one at the top of the bounds
     * *                 if topFocus is true, or at the bottom of the bounds if topFocus is
     * *                 false
     * *
     * @param top      the top offset of the bounds in which a focusable must be
     * *                 found
     * *
     * @param bottom   the bottom offset of the bounds in which a focusable must
     * *                 be found
     * *
     * @return the next focusable component in the bounds or null if none can
     * * be found
     */
    private fun findFocusableViewInBounds(topFocus: Boolean, top: Int, bottom: Int, leftFocus: Boolean, left: Int, right: Int): View? {
        val focusables = getFocusables(View.FOCUS_FORWARD)
        var focusCandidate: View? = null

        /*
   * A fully contained focusable is one where its top is below the bound's
   * top, and its bottom is above the bound's bottom. A partially
   * contained focusable is one where some part of it is within the
   * bounds, but it also has some part that is not within bounds.  A fully contained
   * focusable is preferred to a partially contained focusable.
   */
        var foundFullyContainedFocusable = false

        val count = focusables.size
        for (i in 0..count - 1) {
            val view = focusables[i]
            val viewTop = view.top
            val viewBottom = view.bottom
            val viewLeft = view.left
            val viewRight = view.right

            if (top < viewBottom && viewTop < bottom && left < viewRight && viewLeft < right) {
                /*
       * the focusable is in the target area, it is a candidate for
       * focusing
       */
                val viewIsFullyContained = top < viewTop && viewBottom < bottom && left < viewLeft && viewRight < right
                if (focusCandidate == null) {
                    /* No candidate, take this one */
                    focusCandidate = view
                    foundFullyContainedFocusable = viewIsFullyContained
                } else {
                    val viewIsCloserToVerticalBoundary = topFocus && viewTop < focusCandidate.top || !topFocus && viewBottom > focusCandidate.bottom
                    val viewIsCloserToHorizontalBoundary = leftFocus && viewLeft < focusCandidate.left || !leftFocus && viewRight > focusCandidate.right
                    if (foundFullyContainedFocusable) {
                        if (viewIsFullyContained && viewIsCloserToVerticalBoundary && viewIsCloserToHorizontalBoundary) {
                            /*
              * We're dealing with only fully contained views, so
              * it has to be closer to the boundary to beat our
              * candidate
              */
                            focusCandidate = view
                        }
                    } else {
                        if (viewIsFullyContained) {
                            /* Any fully contained view beats a partially contained view */
                            focusCandidate = view
                            foundFullyContainedFocusable = true
                        } else if (viewIsCloserToVerticalBoundary && viewIsCloserToHorizontalBoundary) {
                            /*
              * Partially contained view beats another partially
              * contained view if it's closer
              */
                            focusCandidate = view
                        }
                    }
                }
            }
        }
        return focusCandidate
    }

    /**
     *
     * Handles scrolling in response to a "home/end" shortcut press. This
     * method will scroll the view to the top or bottom and give the focus
     * to the topmost/bottommost component in the new visible area. If no
     * component is a good candidate for focus, this scrollview reclaims the
     * focus.

     * @param direction the scroll direction: [android.view.View.FOCUS_UP]
     * *                  to go the top of the view or
     * *                  [android.view.View.FOCUS_DOWN] to go the bottom
     * *
     * @return true if the key event is consumed by this method, false otherwise
     */
    fun fullScroll(direction: Int, horizontal: Boolean): Boolean {
        if (!horizontal) {
            val down = direction == View.FOCUS_DOWN
            val height = height
            mTempRect.top = 0
            mTempRect.bottom = height
            if (down) {
                val count = childCount
                if (count > 0) {
                    val view = getChildAt(count - 1)
                    mTempRect.bottom = view.bottom
                    mTempRect.top = mTempRect.bottom - height
                }
            }
            return scrollAndFocus(direction, mTempRect.top, mTempRect.bottom, 0, 0, 0)
        } else {
            val right = direction == View.FOCUS_DOWN
            val width = width
            mTempRect.left = 0
            mTempRect.right = width
            if (right) {
                val count = childCount
                if (count > 0) {
                    val view = getChildAt(count - 1)
                    mTempRect.right = view.bottom
                    mTempRect.left = mTempRect.right - width
                }
            }
            return scrollAndFocus(0, 0, 0, direction, mTempRect.top, mTempRect.bottom)
        }
    }

    /**
     *
     * Scrolls the view to make the area defined by `top` and
     * `bottom` visible. This method attempts to give the focus
     * to a component visible in this area. If no component can be focused in
     * the new visible area, the focus is reclaimed by this scrollview.

     * @param direction the scroll direction: [android.view.View.FOCUS_UP]
     * *                  to go upward
     * *                  [android.view.View.FOCUS_DOWN] to downward
     * *
     * @param top       the top offset of the new area to be made visible
     * *
     * @param bottom    the bottom offset of the new area to be made visible
     * *
     * @return true if the key event is consumed by this method, false otherwise
     */
    private fun scrollAndFocus(directionY: Int, top: Int, bottom: Int, directionX: Int, left: Int, right: Int): Boolean {
        var handled = true
        val height = height
        val containerTop = scrollY
        val containerBottom = containerTop + height
        val up = directionY == View.FOCUS_UP
        val width = width
        val containerLeft = scrollX
        val containerRight = containerLeft + width
        val leftwards = directionX == View.FOCUS_UP
        var newFocused: View? = findFocusableViewInBounds(up, top, bottom, leftwards, left, right)
        if (newFocused == null) {
            newFocused = this
        }
        if (top >= containerTop && bottom <= containerBottom || left >= containerLeft && right <= containerRight) {
            handled = false
        } else {
            val deltaY = if (up) top - containerTop else bottom - containerBottom
            val deltaX = if (leftwards) left - containerLeft else right - containerRight
            doScroll(deltaX, deltaY)
        }
        if (newFocused !== findFocus() && newFocused.requestFocus(directionY)) {
            mTwoDScrollViewMovedFocus = true
            mTwoDScrollViewMovedFocus = false
        }
        return handled
    }

    /**
     * Handle scrolling in response to an up or down arrow click.

     * @param direction The direction corresponding to the arrow key that was
     * *                  pressed
     * *
     * @return True if we consumed the event, false otherwise
     */
    fun arrowScroll(direction: Int, horizontal: Boolean): Boolean {
        var currentFocused: View? = findFocus()
        if (currentFocused === this) currentFocused = null
        val nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction)
        val maxJump = if (horizontal) maxScrollAmountHorizontal else maxScrollAmountVertical

        if (!horizontal) {
            if (nextFocused != null) {
                nextFocused.getDrawingRect(mTempRect)
                offsetDescendantRectToMyCoords(nextFocused, mTempRect)
                val scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect)
                doScroll(0, scrollDelta)
                nextFocused.requestFocus(direction)
            } else {
                // no new focus
                var scrollDelta = maxJump
                if (direction == View.FOCUS_UP && scrollY < scrollDelta) {
                    scrollDelta = scrollY
                } else if (direction == View.FOCUS_DOWN) {
                    if (childCount > 0) {
                        val daBottom = getChildAt(0).bottom
                        val screenBottom = scrollY + height
                        if (daBottom - screenBottom < maxJump) {
                            scrollDelta = daBottom - screenBottom
                        }
                    }
                }
                if (scrollDelta == 0) {
                    return false
                }
                doScroll(0, if (direction == View.FOCUS_DOWN) scrollDelta else -scrollDelta)
            }
        } else {
            if (nextFocused != null) {
                nextFocused.getDrawingRect(mTempRect)
                offsetDescendantRectToMyCoords(nextFocused, mTempRect)
                val scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect)
                doScroll(scrollDelta, 0)
                nextFocused.requestFocus(direction)
            } else {
                // no new focus
                var scrollDelta = maxJump
                if (direction == View.FOCUS_UP && scrollY < scrollDelta) {
                    scrollDelta = scrollY
                } else if (direction == View.FOCUS_DOWN) {
                    if (childCount > 0) {
                        val daBottom = getChildAt(0).bottom
                        val screenBottom = scrollY + height
                        if (daBottom - screenBottom < maxJump) {
                            scrollDelta = daBottom - screenBottom
                        }
                    }
                }
                if (scrollDelta == 0) {
                    return false
                }
                doScroll(if (direction == View.FOCUS_DOWN) scrollDelta else -scrollDelta, 0)
            }
        }
        return true
    }

    /**
     * Smooth scroll by a Y delta

     * @param delta the number of pixels to scroll by on the Y axis
     */
    private fun doScroll(deltaX: Int, deltaY: Int) {
        if (deltaX != 0 || deltaY != 0) {
            smoothScrollBy(deltaX, deltaY)
        }
    }

    /**
     * Like [View.scrollBy], but scroll smoothly instead of immediately.

     * @param dx the number of pixels to scroll by on the X axis
     * *
     * @param dy the number of pixels to scroll by on the Y axis
     */
    fun smoothScrollBy(dx: Int, dy: Int) {
        val duration = AnimationUtils.currentAnimationTimeMillis() - mLastScroll
        if (duration > ANIMATED_SCROLL_GAP) {
            mScroller!!.startScroll(scrollX, scrollY, dx, dy)
            awakenScrollBars(mScroller!!.duration)
            invalidate()
        } else {
            if (!mScroller!!.isFinished) {
                mScroller!!.abortAnimation()
            }
            scrollBy(dx, dy)
        }
        mLastScroll = AnimationUtils.currentAnimationTimeMillis()
    }

    /**
     * Like [.scrollTo], but scroll smoothly instead of immediately.

     * @param x the position where to scroll on the X axis
     * *
     * @param y the position where to scroll on the Y axis
     */
    fun smoothScrollTo(x: Int, y: Int) {
        smoothScrollBy(x - scrollX, y - scrollY)
    }

    /**
     *
     * The scroll range of a scroll view is the overall height of all of its
     * children.
     */
    override fun computeVerticalScrollRange(): Int {
        val count = childCount
        return if (count == 0) height else getChildAt(0).bottom
    }

    override fun computeHorizontalScrollRange(): Int {
        val count = childCount
        return if (count == 0) width else getChildAt(0).right
    }

    override fun measureChild(child: View, parentWidthMeasureSpec: Int, parentHeightMeasureSpec: Int) {
        val lp = child.layoutParams
        val childWidthMeasureSpec: Int
        val childHeightMeasureSpec: Int

        childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(parentWidthMeasureSpec, paddingLeft + paddingRight, lp.width)
        childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun measureChildWithMargins(child: View, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int) {
        val lp = child.layoutParams as ViewGroup.MarginLayoutParams
        val childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(lp.leftMargin + lp.rightMargin, View.MeasureSpec.UNSPECIFIED)
        val childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(lp.topMargin + lp.bottomMargin, View.MeasureSpec.UNSPECIFIED)

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun computeScroll() {
        if (mScroller!!.computeScrollOffset()) {
            // This is called at drawing time by ViewGroup.  We don't want to
            // re-show the scrollbars at this point, which scrollTo will do,
            // so we replicate most of scrollTo here.
            //
            //         It's a little odd to call onScrollChanged from inside the drawing.
            //
            //         It is, except when you remember that computeScroll() is used to
            //         animate scrolling. So unless we want to defer the onScrollChanged()
            //         until the end of the animated scrolling, we don't really have a
            //         choice here.
            //
            //         I agree.  The alternative, which I think would be worse, is to post
            //         something and tell the subclasses later.  This is bad because there
            //         will be a window where mScrollX/Y is different from what the app
            //         thinks it is.
            //
            val oldX = scrollX
            val oldY = scrollY
            val x = mScroller!!.currX
            val y = mScroller!!.currY
            if (childCount > 0) {
                val child = getChildAt(0)
                scrollTo(clamp(x, width - paddingRight - paddingLeft, child.width),
                        clamp(y, height - paddingBottom - paddingTop, child.height))
            } else {
                scrollTo(x, y)
            }
            if (oldX != scrollX || oldY != scrollY) {
                onScrollChanged(scrollX, scrollY, oldX, oldY)
            }

            // Keep on drawing until the animation has finished.
            postInvalidate()
        }
    }

    /**
     * Scrolls the view to the given child.

     * @param child the View to scroll to
     */
    private fun scrollToChild(child: View) {
        child.getDrawingRect(mTempRect)
        /* Offset from child's local coordinates to TwoDScrollView coordinates */
        offsetDescendantRectToMyCoords(child, mTempRect)
        val scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect)
        if (scrollDelta != 0) {
            scrollBy(0, scrollDelta)
        }
    }

    /**
     * If rect is off screen, scroll just enough to get it (or at least the
     * first screen size chunk of it) on screen.

     * @param rect      The rectangle.
     * *
     * @param immediate True to scroll immediately without animation
     * *
     * @return true if scrolling was performed
     */
    private fun scrollToChildRect(rect: Rect, immediate: Boolean): Boolean {
        val delta = computeScrollDeltaToGetChildRectOnScreen(rect)
        val scroll = delta != 0
        if (scroll) {
            if (immediate) {
                scrollBy(0, delta)
            } else {
                smoothScrollBy(0, delta)
            }
        }
        return scroll
    }

    /**
     * Compute the amount to scroll in the Y direction in order to get
     * a rectangle completely on the screen (or, if taller than the screen,
     * at least the first screen size chunk of it).

     * @param rect The rect.
     * *
     * @return The scroll delta.
     */
    protected fun computeScrollDeltaToGetChildRectOnScreen(rect: Rect): Int {
        if (childCount == 0) return 0
        val height = height
        var screenTop = scrollY
        var screenBottom = screenTop + height
        val fadingEdge = verticalFadingEdgeLength
        // leave room for top fading edge as long as rect isn't at very top
        if (rect.top > 0) {
            screenTop += fadingEdge
        }

        // leave room for bottom fading edge as long as rect isn't at very bottom
        if (rect.bottom < getChildAt(0).height) {
            screenBottom -= fadingEdge
        }
        var scrollYDelta = 0
        if (rect.bottom > screenBottom && rect.top > screenTop) {
            // need to move down to get it in view: move down just enough so
            // that the entire rectangle is in view (or at least the first
            // screen size chunk).
            if (rect.height() > height) {
                // just enough to get screen size chunk on
                scrollYDelta += rect.top - screenTop
            } else {
                // get entire rect at bottom of screen
                scrollYDelta += rect.bottom - screenBottom
            }

            // make sure we aren't scrolling beyond the end of our content
            val bottom = getChildAt(0).bottom
            val distanceToBottom = bottom - screenBottom
            scrollYDelta = Math.min(scrollYDelta, distanceToBottom)

        } else if (rect.top < screenTop && rect.bottom < screenBottom) {
            // need to move up to get it in view: move up just enough so that
            // entire rectangle is in view (or at least the first screen
            // size chunk of it).

            if (rect.height() > height) {
                // screen size chunk
                scrollYDelta -= screenBottom - rect.bottom
            } else {
                // entire rect at top
                scrollYDelta -= screenTop - rect.top
            }

            // make sure we aren't scrolling any further than the top our content
            scrollYDelta = Math.max(scrollYDelta, -scrollY)
        }
        return scrollYDelta
    }

    override fun requestChildFocus(child: View, focused: View) {
        if (!mTwoDScrollViewMovedFocus) {
            if (!mIsLayoutDirty) {
                scrollToChild(focused)
            } else {
                // The child may not be laid out yet, we can't compute the scroll yet
                mChildToScrollTo = focused
            }
        }
        super.requestChildFocus(child, focused)
    }

    /**
     * When looking for focus in children of a scroll view, need to be a little
     * more careful not to give focus to something that is scrolled off screen.
     *
     *
     * This is more expensive than the default [android.view.ViewGroup]
     * implementation, otherwise this behavior might have been made the default.
     */
    override fun onRequestFocusInDescendants(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        var direction = direction
        // convert from forward / backward notation to up / down / left / right
        // (ugh).
        if (direction == View.FOCUS_FORWARD) {
            direction = View.FOCUS_DOWN
        } else if (direction == View.FOCUS_BACKWARD) {
            direction = View.FOCUS_UP
        }

        val nextFocus = (if (previouslyFocusedRect == null)
            FocusFinder.getInstance().findNextFocus(this, null, direction)
        else
            FocusFinder.getInstance().findNextFocusFromRect(this,
                    previouslyFocusedRect, direction)) ?: return false

        return nextFocus.requestFocus(direction, previouslyFocusedRect)
    }

    override fun requestChildRectangleOnScreen(child: View, rectangle: Rect, immediate: Boolean): Boolean {
        // offset into coordinate space of this scroll view
        rectangle.offset(child.left - child.scrollX, child.top - child.scrollY)
        return scrollToChildRect(rectangle, immediate)
    }

    override fun requestLayout() {
        mIsLayoutDirty = true
        super.requestLayout()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        mIsLayoutDirty = false
        // Give a child focus if it needs it
        if (mChildToScrollTo != null && isViewDescendantOf(mChildToScrollTo!!, this)) {
            scrollToChild(mChildToScrollTo!!)
        }
        mChildToScrollTo = null

        // Calling this with the present values causes it to re-clam them
        scrollTo(scrollX, scrollY)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val currentFocused = findFocus()
        if (null == currentFocused || this === currentFocused)
            return

        // If the currently-focused view was visible on the screen when the
        // screen was at the old height, then scroll the screen to make that
        // view visible with the new screen height.
        currentFocused.getDrawingRect(mTempRect)
        offsetDescendantRectToMyCoords(currentFocused, mTempRect)
        val scrollDeltaX = computeScrollDeltaToGetChildRectOnScreen(mTempRect)
        val scrollDeltaY = computeScrollDeltaToGetChildRectOnScreen(mTempRect)
        doScroll(scrollDeltaX, scrollDeltaY)
    }

    /**
     * Return true if child is an descendant of parent, (or equal to the parent).
     */
    private fun isViewDescendantOf(child: View, parent: View): Boolean {
        if (child === parent) {
            return true
        }

        val theParent = child.parent
        return theParent is ViewGroup && isViewDescendantOf(theParent as View, parent)
    }

    /**
     * Fling the scroll view

     * @param velocityY The initial velocity in the Y direction. Positive
     * *                  numbers mean that the finger/curor is moving down the screen,
     * *                  which means we want to scroll towards the top.
     */
    fun fling(velocityX: Int, velocityY: Int) {
        if (childCount > 0) {
            val height = height - paddingBottom - paddingTop
            val bottom = getChildAt(0).height
            val width = width - paddingRight - paddingLeft
            val right = getChildAt(0).width

            mScroller!!.fling(scrollX, scrollY, velocityX, velocityY, 0, right - width, 0, bottom - height)

            val movingDown = velocityY > 0
            val movingRight = velocityX > 0

            var newFocused: View? = findFocusableViewInMyBounds(movingRight, mScroller!!.finalX, movingDown, mScroller!!.finalY, findFocus())
            if (newFocused == null) {
                newFocused = this
            }

            if (newFocused !== findFocus() && newFocused.requestFocus(if (movingDown) View.FOCUS_DOWN else View.FOCUS_UP)) {
                mTwoDScrollViewMovedFocus = true
                mTwoDScrollViewMovedFocus = false
            }

            awakenScrollBars(mScroller!!.duration)
            invalidate()
        }
    }

    /**
     * {@inheritDoc}
     *
     *
     *
     * This version also clamps the scrolling to the bounds of our child.
     */
    override fun scrollTo(x: Int, y: Int) {
        var x = x
        var y = y
        // we rely on the fact the View.scrollBy calls scrollTo.
        if (childCount > 0) {
            val child = getChildAt(0)
            x = clamp(x, width - paddingRight - paddingLeft, child.width)
            y = clamp(y, height - paddingBottom - paddingTop, child.height)
            if (x != scrollX || y != scrollY) {
                super.scrollTo(x, y)
            }
        }
    }

    private fun clamp(n: Int, my: Int, child: Int): Int {
        if (my >= child || n < 0) {
            /* my >= child is this case:
      *                    |--------------- me ---------------|
      *     |------ child ------|
      * or
      *     |--------------- me ---------------|
      *            |------ child ------|
      * or
      *     |--------------- me ---------------|
      *                                  |------ child ------|
      *
      * n < 0 is this case:
      *     |------ me ------|
      *                    |-------- child --------|
      *     |-- mScrollX --|
      */
            return 0
        }
        if (my + n > child) {
            /* this case:
      *                    |------ me ------|
      *     |------ child ------|
      *     |-- mScrollX --|
      */
            return child - my
        }
        return n
    }

    companion object {
        internal val ANIMATED_SCROLL_GAP = 250
        internal val MAX_SCROLL_FACTOR = 0.5f
    }
}
