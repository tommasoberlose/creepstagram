package com.tommasoberlose.creepstagram.constants

/**
 * Created by tommaso on 09/02/18.
 */

enum class MessageEventCode private constructor(val code: Int) {
  TAKE_SCREENSHOT(0),
  SHOW_SCREENSHOT(1),
  VIEW_READY(2),
  SCREENSHOT_ERROR(3),
  SAVE_SCREENSHOT(4),
  SHARE_SCREENSHOT(5)
}