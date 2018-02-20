package com.tommasoberlose.creepstagram.utils.extensions

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout

/**
 * Created by tommaso on 09/02/18.
 */
fun View.expand() {
  this.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
  val targtetHeight = this.measuredHeight

  this.layoutParams.height = 0
  this.visibility = View.VISIBLE
  val a = object : Animation() {
    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
      this@expand.layoutParams.height = if (interpolatedTime == 1f)
        LinearLayout.LayoutParams.WRAP_CONTENT
      else
        (targtetHeight * interpolatedTime).toInt()
      this@expand.requestLayout()
    }

    override fun willChangeBounds(): Boolean {
      return true
    }
  }

  a.duration = 500 //(targtetHeight / this.context.resources.displayMetrics.density).toInt().toLong()
  this.startAnimation(a)
}

fun View.collapse() {
  val initialHeight = this.measuredHeight

  val a = object : Animation() {
    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
      if (interpolatedTime == 1f) {
        this@collapse.visibility = View.GONE
      } else {
        this@collapse.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
        this@collapse.requestLayout()
      }
    }

    override fun willChangeBounds(): Boolean {
      return true
    }
  }

  a.duration = (initialHeight / this.context.resources.displayMetrics.density).toInt().toLong()
  this.startAnimation(a)
}