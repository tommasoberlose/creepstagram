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
import android.content.Context
import android.provider.Settings
import android.view.View


/**
 * Created by tommaso on 09/02/18.
 */

object Util {

  fun canDrawOverlay(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
  }
}