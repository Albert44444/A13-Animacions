package com.example.balls

import android.animation.*
import android.content.Context
import android.graphics.Canvas
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

class MyAnimationView(context: Context?) : View(context) {
    private val balls: ArrayList<ShapeHolder> = ArrayList()

    init {
        val colorAnim: ValueAnimator = ObjectAnimator.ofInt(this, "backgroundColor", RED, BLUE)
        colorAnim.duration = 3000
        colorAnim.setEvaluator(ArgbEvaluator())
        colorAnim.repeatCount = ValueAnimator.INFINITE
        colorAnim.repeatMode = ValueAnimator.REVERSE
        colorAnim.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN && event.action != MotionEvent.ACTION_MOVE) {
            return false
        }
        val newBall = addBall(event.x, event.y)
        val startY = newBall.y
        val endY = height.toFloat() - newBall.height
        val midY = (endY + startY) / 2

        val h = height.toFloat()
        val eventY = event.y
        val duration = (500 * ((h - eventY) / h)).toInt()

        val bounceAnim = ObjectAnimator.ofFloat(newBall, "y", startY, endY)
        bounceAnim.duration = duration.toLong()
        bounceAnim.interpolator = AccelerateInterpolator()

        val bounceBackAnim = ObjectAnimator.ofFloat(newBall, "y", endY, midY)
        bounceBackAnim.duration = duration.toLong()
        bounceBackAnim.interpolator = DecelerateInterpolator()

        val animatorSet = AnimatorSet()
        animatorSet.play(bounceAnim).before(bounceBackAnim)

        val fadeAnim = ObjectAnimator.ofFloat(newBall, "alpha", 1f, 0f)
        fadeAnim.duration = 250
        fadeAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                balls.remove((animation as ObjectAnimator).target)
            }
        })

        val finalAnimatorSet = AnimatorSet()
        finalAnimatorSet.play(animatorSet).before(fadeAnim)
        finalAnimatorSet.start()

        return true
    }

    private fun addBall(x: Float, y: Float): ShapeHolder {
        val circle = OvalShape()
        circle.resize(50f, 50f)
        val drawable = ShapeDrawable(circle)
        val shapeHolder = ShapeHolder(drawable)
        shapeHolder.x = x - 25f
        shapeHolder.y = y - 25f
        val red = (Math.random() * 255).toInt()
        val green = (Math.random() * 255).toInt()
        val blue = (Math.random() * 255).toInt()
        val color = -0x1000000 or (red shl 16) or (green shl 8) or blue
        val paint = drawable.paint
        val darkColor = -0x1000000 or (red / 4 shl 16) or (green / 4 shl 8) or blue / 4
        val gradient = RadialGradient(37.5f, 12.5f, 50f, color, darkColor, Shader.TileMode.CLAMP)
        paint.shader = gradient
        shapeHolder.paint = paint
        balls.add(shapeHolder)
        return shapeHolder
    }

    override fun onDraw(canvas: Canvas) {
        for (shapeHolder in balls) {
            canvas.save()
            canvas.translate(shapeHolder.x, shapeHolder.y)
            shapeHolder.shape.draw(canvas)
            canvas.restore()
        }
    }

    companion object {
        private const val RED = -0x7f80
        private const val BLUE = -0x7f7f01
    }
}
