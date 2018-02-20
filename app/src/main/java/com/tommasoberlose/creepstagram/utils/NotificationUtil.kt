package com.tommasoberlose.creepstagram.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.tommasoberlose.creepstagram.R
import com.tommasoberlose.creepstagram.constants.GlobalConstants
import com.tommasoberlose.creepstagram.ui.MainActivity
import com.tommasoberlose.creepstagram.ui.ScreenshotActivity



/**
 * Created by tommaso on 09/02/18.
 */



object NotificationUtil {
  private const val NOTIFICATION_ID = 1
  private const val OPEN_SCREENSHOT_NOTIFICATION_ID = 10
  private const val INTENT_REQUEST_CODE = 2
  private const val OPEN_SCREENSHOT_CODE = 12

  fun showScreenshotNotification(context: Context) {
    val mNotificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, "service")
        .setSmallIcon(R.drawable.ic_stat_screenshot)
        .setPriority(Notification.PRIORITY_LOW)
        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
        .setContentTitle(context.getString(R.string.running_service_notification_title))
        .setContentText(context.getString(R.string.running_service_notification_subtitle))
        .setAutoCancel(false)
        .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.running_service_notification_subtitle)))

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      mNotificationManager.createNotificationChannel(NotificationChannel("service", "Running service notification", NotificationManager.IMPORTANCE_LOW))
    }

    val intent = Intent(context, MainActivity::class.java)
    intent.action = GlobalConstants.HIDE_CONTROLLERS_ACTION
    val pi: PendingIntent = PendingIntent.getActivity(context, INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    mBuilder.setContentIntent(pi)
    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build())
  }

  fun hideScreenshotNotification(context: Context) {
    val mNotificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    mNotificationManager.cancel(NOTIFICATION_ID)
  }

  fun showSavedScreenshotNotification(context: Context, imagePath: String, bitmap: Bitmap?) {
    if (bitmap != null) {
      val mNotificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, "util")
          .setSmallIcon(R.drawable.ic_stat_screenshot)
          .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
          .setContentTitle(context.getString(R.string.screenshot_notification_title))
          .setContentText(context.getString(R.string.screenshot_notification_subtitle))
          .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
          .setAutoCancel(true)

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        mNotificationManager.createNotificationChannel(NotificationChannel("util", "Screenshots notification", NotificationManager.IMPORTANCE_DEFAULT))
      }

      val intent = Intent()
      intent.action = android.content.Intent.ACTION_VIEW
      val uri = Uri.parse(imagePath)
      intent.setDataAndType(uri, "image/*")
      val pi: PendingIntent = PendingIntent.getActivity(context, OPEN_SCREENSHOT_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
      mBuilder.setContentIntent(pi)
      mNotificationManager.notify(OPEN_SCREENSHOT_NOTIFICATION_ID, mBuilder.build())
    }
  }
}