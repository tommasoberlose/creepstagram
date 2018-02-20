package com.tommasoberlose.creepstagram.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.view.Window
import java.io.File
import java.io.FileOutputStream
import java.util.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.R.attr.bitmap
import android.view.View


/**
 * Created by tommaso on 09/02/18.
 */

object ScreenshotUtil {

  fun takeScreenshot(view: View): Bitmap? {
    return try {
      view.isDrawingCacheEnabled = true
      view.buildDrawingCache()
      val b1 = view.drawingCache

      val bitmap = Bitmap.createBitmap(b1)
      val canvas = Canvas(bitmap)
      view.draw(canvas)
      view.isDrawingCacheEnabled = false

      bitmap
    } catch (e: Throwable) {
      null
    }
  }

}