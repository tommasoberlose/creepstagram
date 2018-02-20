package com.tommasoberlose.creepstagram.services

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.IBinder
import android.view.View
import android.widget.ImageView
import com.tommasoberlose.creepstagram.R
import android.view.MotionEvent
import android.view.WindowManager
import android.view.Gravity
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.RelativeLayout
import com.tommasoberlose.creepstagram.ui.ScreenshotActivity
import com.tommasoberlose.creepstagram.R.layout.floating_view
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.toast
import java.util.*
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.provider.Settings.canDrawOverlays
import com.tommasoberlose.creepstagram.ui.MainActivity
import com.tommasoberlose.creepstagram.utils.NotificationUtil


/**
 * Created by tommaso on 16/02/18.
 */
class OverlayService : Service() {

  private var windowManager: WindowManager? = null
  private var chatHead: View? = null

  override fun onBind(intent: Intent): IBinder? {
    // TODO Auto-generated method stub
    return null
  }

  var newX = 0f
  var newY = 0f
  private val MAX_CLICK_DURATION = 200
  private var startClickTime: Long = 0

  override fun onCreate() {
    super.onCreate()

    NotificationUtil.showScreenshotNotification(this)

    windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    if (!Settings.canDrawOverlays(this)) {
      val myIntent = Intent(this, MainActivity::class.java)
      startActivity(myIntent)
      stopSelf()
    }

    chatHead = View.inflate(this,R.layout.floating_view, null)

    val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams(
          WindowManager.LayoutParams.WRAP_CONTENT,
          WindowManager.LayoutParams.WRAP_CONTENT,
          WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
          WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
          PixelFormat.TRANSLUCENT)
    } else {
      WindowManager.LayoutParams(
          WindowManager.LayoutParams.WRAP_CONTENT,
          WindowManager.LayoutParams.WRAP_CONTENT,
          WindowManager.LayoutParams.TYPE_PHONE,
          WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
          PixelFormat.TRANSLUCENT)
    }

    params.gravity = Gravity.BOTTOM or Gravity.END
    val margin = (32 * Resources.getSystem().displayMetrics.density).toInt()
    params.x = margin
    params.y = margin

    windowManager!!.addView(chatHead, params)
    chatHead!!.animate().scaleX(1f).scaleY(1f).rotation(0f).start()


    var deltaX = 0
    var deltaY = 0
    var eventTouchX = 0
    var eventTouchY = 0

    try {
      chatHead!!.setOnTouchListener { v, event ->
        when(event.action) {
          MotionEvent.ACTION_DOWN -> {
            startClickTime = Calendar.getInstance().timeInMillis
            eventTouchX = event.rawX.toInt()
            eventTouchY = event.rawY.toInt()
            newX = params.x.toFloat()
            newY = params.y.toFloat()
            true
          }
          MotionEvent.ACTION_MOVE -> {
            params.x = - ((event.rawX - eventTouchX) - newX).toInt()
            params.y = - ((event.rawY - eventTouchY) - newY).toInt()
            windowManager!!.updateViewLayout(chatHead, params)
            true
          }
          MotionEvent.ACTION_UP -> {
            val clickDuration = Calendar.getInstance().timeInMillis - startClickTime
            if (clickDuration < MAX_CLICK_DURATION) {
              v.animate().rotation(v.rotation + 180f).alpha(0f).setListener(object: Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                  startActivity(Intent(this@OverlayService, ScreenshotActivity::class.java))
                  stopSelf()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

              }).start()
            }
            true
          }
          else -> {
          true
          }
        }
      }
    } catch (e: Exception) {
      toast(R.string.bitmap_error)
      stopSelf()
    }

  }

  override fun onDestroy() {
    super.onDestroy()
    if (chatHead != null) windowManager!!.removeView(chatHead)
    NotificationUtil.hideScreenshotNotification(this)
  }

}