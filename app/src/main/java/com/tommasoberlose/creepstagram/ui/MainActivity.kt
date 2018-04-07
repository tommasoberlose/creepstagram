package com.tommasoberlose.creepstagram.ui

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tommasoberlose.creepstagram.R
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.pm.ShortcutInfoCompat
import android.support.v4.content.pm.ShortcutManagerCompat
import android.support.v4.graphics.drawable.IconCompat
import android.util.Log
import android.view.View
import com.tommasoberlose.creepstagram.constants.GlobalConstants
import com.tommasoberlose.creepstagram.constants.GlobalConstants.HIDE_CONTROLLERS_ACTION
import com.tommasoberlose.creepstagram.constants.GlobalConstants.REQUEST_OVERLAY_PERMISSION_CODE
import com.tommasoberlose.creepstagram.constants.GlobalConstants.SHOW_CONTROLLERS_ACTION
import com.tommasoberlose.creepstagram.services.OverlayService
import com.tommasoberlose.creepstagram.utils.NotificationUtil
import com.tommasoberlose.creepstagram.utils.extensions.collapse
import com.tommasoberlose.creepstagram.utils.extensions.expand
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (intent.action == Intent.ACTION_CREATE_SHORTCUT) {
      val shortcutIntent = Intent(this, MainActivity::class.java)
      shortcutIntent.action = SHOW_CONTROLLERS_ACTION
      val shortcut = ShortcutInfoCompat.Builder(this, "show_controllers")
          .setIntent(shortcutIntent)
          .setShortLabel(getString(R.string.shortcut_label))
          .setIcon(IconCompat.createWithResource(this, R.mipmap.ic_shortcut))
          .setLongLabel(getString(R.string.shortcut_label))
          .build()

      val pinnedShortcutCallbackIntent = ShortcutManagerCompat.createShortcutResultIntent(this, shortcut)
      setResult(Activity.RESULT_OK, pinnedShortcutCallbackIntent)
      finish()
    } else if (intent.action == SHOW_CONTROLLERS_ACTION) {
      startService(Intent(this, OverlayService::class.java))
      finish()
    } else if (intent.action == HIDE_CONTROLLERS_ACTION) {
      stopService(Intent(this, OverlayService::class.java))
      finish()
    }
    setContentView(R.layout.activity_main)

    name.typeface = Typeface.createFromAsset(assets, "fonts/pacifico_regular.ttf")

    footer_logo.setOnClickListener {
      it.animate().rotation(it.rotation + 180).start()
    }
  }

  override fun onResume() {
    super.onResume()
    updateUI()
  }

  fun updateUI() {
    val canIDrawOverlay = Settings.canDrawOverlays(this)
    main_title.text = getString(if (!canIDrawOverlay) R.string.ask_overlay_title else R.string.main_page_title)
    main_subtitle.text = getString(if (!canIDrawOverlay) R.string.ask_overlay_subtitle else R.string.main_page_subtitle)
    action_request_permission.text = getString(if (!canIDrawOverlay) R.string.ask_overlay_button_request else R.string.shortcut_label)
    group_bg.background = getDrawable(if (!canIDrawOverlay) R.drawable.gradient_bg_disabled else R.drawable.gradient_background)
    name.setTextColor(getColor(if (!canIDrawOverlay) R.color.colorPrimaryDisabled else R.color.colorAccent))
    if (canIDrawOverlay) {
      action_request_permission.setOnClickListener {
        startService(Intent(this, OverlayService::class.java))
        finish()
      }
    } else {
      action_request_permission.setOnClickListener {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + applicationContext.packageName))
        startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION_CODE)
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_OVERLAY_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
      updateUI()
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

}
