package com.tommasoberlose.creepstagram.ui.intro

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color.parseColor
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.AppIntro
import com.tommasoberlose.creepstagram.R


class IntroActivity : AppIntro() {
  override fun onCreate(@Nullable savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Instead of fragments, you can also use our default slide
    // Just set a title, description, background and image. AppIntro will do the rest.
    addSlide(AppIntroFragment.newInstance(getString(R.string.app_name), "", "desc", "", R.mipmap.ic_launcher, R.drawable.gradient_background, getColor(android.R.color.white), getColor(android.R.color.white)))
    addSlide(AppIntroFragment.newInstance(getString(R.string.app_name), "", "desc", "", R.mipmap.ic_launcher, R.drawable.gradient_background, getColor(android.R.color.white), getColor(android.R.color.white)))
    addSlide(AppIntroFragment.newInstance(getString(R.string.app_name), "", "desc", "", R.mipmap.ic_launcher, R.drawable.gradient_background, getColor(android.R.color.white), getColor(android.R.color.white)))

    // OPTIONAL METHODS
    // Override bar/separator color.
    setBarColor(Color.parseColor("#3F51B5"))
    setSeparatorColor(Color.parseColor("#2196F3"))

    // Hide Skip/Done button.
    showSkipButton(true)
    isProgressButtonEnabled = true
  }

  override fun onSkipPressed(currentFragment: Fragment) {
    super.onSkipPressed(currentFragment)
    finish()
  }

  override fun onDonePressed(currentFragment: Fragment) {
    super.onDonePressed(currentFragment)
    finish()
  }
}
