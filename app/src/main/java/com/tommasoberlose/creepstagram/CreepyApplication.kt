package com.tommasoberlose.creepstagram

import android.app.Application
import io.realm.Realm


/**
 * Created by tommaso on 09/02/18.
 */

class CreepyApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    Realm.init(this)
  }
}