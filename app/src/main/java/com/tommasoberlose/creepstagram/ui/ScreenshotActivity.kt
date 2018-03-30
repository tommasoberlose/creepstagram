package com.tommasoberlose.creepstagram.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tommasoberlose.creepstagram.R
import kotlinx.android.synthetic.main.activity_screenshot.*
import android.os.Handler
import android.util.Log
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.DisplayMetrics
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.BasePermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import com.snatik.storage.Storage
import com.tommasoberlose.creepstagram.services.OverlayService
import com.tommasoberlose.creepstagram.utils.NotificationUtil
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class ScreenshotActivity : AppCompatActivity() {

  private var mScreenDensity: Int = 0

  private var mResultCode: Int = 0
  private var mResultData: Intent? = null

  private var mMediaProjection: MediaProjection? = null
  private var mVirtualDisplay: VirtualDisplay? = null
  private var mMediaProjectionManager: MediaProjectionManager? = null

  private var bitmap: Bitmap? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_screenshot)

    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)
    mScreenDensity = metrics.densityDpi
    mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

    action_delete.setOnClickListener {
      finish()
    }

    action_save.setOnClickListener {
      if (bitmap != null) {
        toast(R.string.action_saving)
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: BasePermissionListener() {
              override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                saveScreenshot()
              }

              override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                token!!.continuePermissionRequest()
              }

              override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                toast(R.string.bitmap_not_saved)
              }
            })
            .check()
      } else {
        toast(R.string.bitmap_error)
        finish()
      }
    }

    action_share.setOnClickListener {
      if (bitmap != null) {
        initShareIntent(this, bitmap)
      } else {
        toast(R.string.bitmap_error)
        finish()
      }
    }

    val mgr = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int,
                                data: Intent) {
    if (requestCode == REQUEST_MEDIA_PROJECTION) {
      if (resultCode != Activity.RESULT_OK) {
        finish()
      } else {
        mResultCode = resultCode
        mResultData = data

        Handler().postDelayed({
          startScreenCapture()
        }, 500)
      }
    }
  }
  fun saveScreenshot() {

    val storage = Storage(this)
    val path = storage.externalStorageDirectory
    val newDir = path + File.separator + "Math Exercises"
    if (!storage.isDirectoryExists(newDir)) {
      storage.createDirectory(newDir)
    }

    val imagePath = newDir + File.separator + Calendar.getInstance().timeInMillis + ".jpeg"
    try {
      storage.createFile(imagePath, Bitmap.createBitmap(bitmap))
      NotificationUtil.showSavedScreenshotNotification(this, imagePath, bitmap)
      finish()
    } catch (e: Exception) {
      e.printStackTrace()
      toast(R.string.bitmap_error)
    }
  }

  override fun onDestroy() {
    tearDownMediaProjection()
    startService(Intent(this, OverlayService::class.java))
    super.onDestroy()
  }

  private fun setUpVirtualDisplay() {
    val imageReader = ImageReader.newInstance(window.decorView.rootView.width, window.decorView.rootView.height, PixelFormat.RGBA_8888, 10)
    imageReader.setOnImageAvailableListener({ reader ->
      var image: Image? = null
      try {
        image = reader.acquireLatestImage()

        val planes = image.planes
        val buffer = planes[0].buffer

        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * imageReader.width

        bitmap = Bitmap.createBitmap(window.decorView.rootView.width + rowPadding / pixelStride, window.decorView.rootView.height, Bitmap.Config.ARGB_8888)
        bitmap!!.copyPixelsFromBuffer(buffer)

      } catch (e: Exception) {
        e.printStackTrace()
      }

      if (bitmap != null) {
        stopScreenCapture()

        window.statusBarColor = getColor(R.color.overlay_background)
        screenshot.setImageBitmap(bitmap)
        content.animate().alpha(1f).start()

      }

    }, null)

    mVirtualDisplay = mMediaProjection!!.createVirtualDisplay("ScreenCapture",
        imageReader.width, imageReader.height, mScreenDensity,
        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
        imageReader.surface, null, null)
  }

  private fun startScreenCapture() {
    Log.d("API", "START CAPTURE")
    if (mMediaProjection != null) {
      setUpVirtualDisplay()
    } else if (mResultCode != 0 && mResultData != null) {
      setUpMediaProjection()
      setUpVirtualDisplay()
    } else {
      startActivityForResult(
          mMediaProjectionManager!!.createScreenCaptureIntent(),
          REQUEST_MEDIA_PROJECTION)
    }
  }

  private fun setUpMediaProjection() {
    mMediaProjection = mMediaProjectionManager!!.getMediaProjection(mResultCode, mResultData!!)
  }

  private fun stopScreenCapture() {
    Log.d("API", "STOP CAPTURE")
    if (mVirtualDisplay == null) {
      return
    }
    mVirtualDisplay!!.release()
    mVirtualDisplay = null
  }

  private fun tearDownMediaProjection() {
    if (mMediaProjection != null) {
      mMediaProjection!!.stop()
      mMediaProjection = null
    }
  }

  fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

    val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
    return Uri.parse(path)
  }

  private fun initShareIntent(context: Context, bitmap: Bitmap?) {
    if (bitmap != null) {
      val shareIntent = Intent()
      shareIntent.action = Intent.ACTION_SEND
      shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(context, bitmap))
      shareIntent.type = "image/JPEG"
      shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
    }
  }

  companion object {
    const val TAG = "ScreenCaptureFragment"
    const val REQUEST_MEDIA_PROJECTION = 3
    const val REQUEST_STORAGE_PERMISSION = 4
  }
}
