package com.tommasoberlose.creepstagram.services

import android.annotation.TargetApi
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.tommasoberlose.creepstagram.R
import com.tommasoberlose.creepstagram.constants.GlobalConstants
import com.tommasoberlose.creepstagram.ui.MainActivity
import com.tommasoberlose.creepstagram.utils.Preference
import org.jetbrains.anko.toast

@TargetApi(Build.VERSION_CODES.N)
class QuickSettingsService : TileService() {
  override fun onTileAdded() {
    super.onTileAdded()
    val pref = Preference(this)
    val tile = qsTile
    tile.icon = Icon.createWithResource(this, R.drawable.ic_stat_screenshot)
    tile.label = getString(R.string.tile_title)
    tile.contentDescription = getString(R.string.app_name)
    tile.state = if (pref.getBooleanValue(GlobalConstants.PREFERENCE_IS_OVERLAY_SERVICE_RUNNING, false)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
    tile.updateTile()

    requestListeningState(this, ComponentName(this, QuickSettingsService::class.java))
  }

  override fun onClick() {
    val tile = qsTile
    val pref = Preference(this)
    if (pref.getBooleanValue(GlobalConstants.PREFERENCE_IS_OVERLAY_SERVICE_RUNNING, false)) {
      val shortcutIntent = Intent(this, MainActivity::class.java)
      shortcutIntent.action = GlobalConstants.HIDE_CONTROLLERS_ACTION
      startActivityAndCollapse(shortcutIntent)
      tile.state = Tile.STATE_INACTIVE
    } else {
      val shortcutIntent = Intent(this, MainActivity::class.java)
      shortcutIntent.action = GlobalConstants.SHOW_CONTROLLERS_ACTION
      startActivityAndCollapse(shortcutIntent)
      tile.state = Tile.STATE_ACTIVE
    }
    tile.updateTile()
  }

  override fun onStartListening() {
    Log.d("CREE", "1")
    super.onStartListening()
    val tile = qsTile
    val pref = Preference(this)
    if (pref.getBooleanValue(GlobalConstants.PREFERENCE_IS_OVERLAY_SERVICE_RUNNING, false)) {
      tile.state = Tile.STATE_INACTIVE
    } else {
      tile.state = Tile.STATE_ACTIVE
    }
    tile.updateTile()
  }
}
