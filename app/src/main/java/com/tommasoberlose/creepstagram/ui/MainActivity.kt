package com.tommasoberlose.creepstagram.ui

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tommasoberlose.creepstagram.R
import android.content.Intent
import android.graphics.Typeface
import android.support.v4.content.pm.ShortcutInfoCompat
import android.support.v4.content.pm.ShortcutManagerCompat
import android.support.v4.graphics.drawable.IconCompat
import com.tommasoberlose.creepstagram.constants.GlobalConstants.HIDE_CONTROLLERS_ACTION
import com.tommasoberlose.creepstagram.constants.GlobalConstants.SHOW_CONTROLLERS_ACTION
import com.tommasoberlose.creepstagram.services.OverlayService
import kotlinx.android.synthetic.main.activity_main.*

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

//    startActivity(Intent(this, IntroActivity::class.java))
  }
}
