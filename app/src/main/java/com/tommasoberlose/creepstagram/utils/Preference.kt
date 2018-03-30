package com.tommasoberlose.creepstagram.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

import java.util.Date

/**
 * Created by tommaso on 02/03/18.
 */

class Preference constructor(val context: Context) {
  protected val mSharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)


  /**
   * Gets boolean value.
   *
   * @param key          the key
   * @param defaultValue the default value
   * @return the boolean value
   */
  fun getBooleanValue(key: String, defaultValue: Boolean): Boolean {
    return mSharedPreferences.getBoolean(key, defaultValue)
  }

  /**
   * Sets boolean value.
   *
   * @param key   the key
   * @param value the value
   */
  fun setBooleanValue(key: String, value: Boolean) {
    val editor = mSharedPreferences.edit()
    editor.putBoolean(key, value)
    editor.commit()
  }

  /**
   * Gets string value.
   *
   * @param key          the key
   * @param defaultValue the default value
   * @return the string value
   */
  fun getStringValue(key: String, defaultValue: String): String? {
    return mSharedPreferences.getString(key, defaultValue)
  }

  /**
   * Sets string value.
   *
   * @param key   the key
   * @param value the value
   */
  fun setStringValue(key: String, value: String) {
    val editor = mSharedPreferences.edit()
    editor.putString(key, value)
    editor.commit()
  }

  /**
   * Gets int value.
   *
   * @param key          the key
   * @param defaultValue the default value
   * @return the int value
   */
  fun getIntValue(key: String, defaultValue: Int): Int {
    return mSharedPreferences.getInt(key, defaultValue)
  }

  /**
   * Sets int value.
   *
   * @param key   the key
   * @param value the value
   */
  fun setIntValue(key: String, value: Int) {
    val editor = mSharedPreferences.edit()
    editor.putInt(key, value)
    editor.commit()
  }

  /**
   * Gets double value.
   *
   * @param key          the key
   * @param defaultValue the default value
   * @return the double value
   */
  fun getDoubleValue(key: String, defaultValue: Double): Double {
    return java.lang.Double.longBitsToDouble(mSharedPreferences.getLong(key, java.lang.Double.doubleToRawLongBits(defaultValue)))
  }

  /**
   * Sets double value.
   *
   * @param key   the key
   * @param value the value
   */
  fun setDoubleValue(key: String, value: Double) {
    val editor = mSharedPreferences.edit()
    editor.putLong(key, java.lang.Double.doubleToRawLongBits(value))
    editor.commit()
  }

  fun getDateValue(key: String, defaultValue: Date): Date {
    val long_val = mSharedPreferences.getLong(key, 0)
    return if (long_val == 0L) defaultValue else Date(long_val)
  }

  fun setDateValue(key: String, date: Date?) {
    val editor = mSharedPreferences.edit()
    if (date == null) {
      editor.putLong(key, 0)
    } else {
      editor.putLong(key, date.time)
    }
    editor.commit()
  }

  /**
   * Gets float value.
   *
   * @param key          the key
   * @param defaultValue the default value
   * @return the float value
   */
  fun getFloatValue(key: String, defaultValue: Float): Float {
    return mSharedPreferences.getFloat(key, defaultValue)
  }

  /**
   * Sets float value.
   *
   * @param key   the key
   * @param value the value
   */
  fun setFloatValue(key: String, value: Float) {
    val editor = mSharedPreferences.edit()
    editor.putFloat(key, value)
    editor.commit()
  }

  fun removeValue(key: String) {
    val editor = mSharedPreferences.edit()
    editor.remove(key)
    editor.commit()
  }

  fun isValueNotEmpty(key: String): Boolean {
    return mSharedPreferences.contains(key)
  }
}