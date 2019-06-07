package com.cschmid.wifialbum

import android.app.Activity
import android.content.res.Resources
import android.graphics.Bitmap
import android.provider.Settings
import android.widget.ImageView
import com.cschmid.wifialbum.Data.FlipDirection
import kotlinx.coroutines.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.cschmid.wifialbum.Activities.MainActivity
import com.cschmid.wifialbum.Data.UserOptions
import com.cschmid.wifialbum.Data.TransitionType


class Gallery (
        imageView1: ImageView,
        imageView2: ImageView
) {

    private val width = Resources.getSystem().displayMetrics.widthPixels.toFloat()

    private val slideLeftTranslateCurrent = TranslateAnimation(0f, -width, 0f, 0f)
    private val slideLeftTranslateNext = TranslateAnimation(width, 0f, 0f, 0f)

    private val slideRightTranslateCurrent = TranslateAnimation(0f, width, 0f, 0f)
    private val slideRightTranslateNext = TranslateAnimation(-width, 0f, 0f, 0f)

    private val fadeOutCurrent = AlphaAnimation(1f, 0f)
    private val fadeInNext = AlphaAnimation(0f, 1f)

    private val imageViews = arrayListOf(imageView1, imageView2)
    private var current = 1

    private lateinit var job: Job

    private var transitionActive = false

    companion object {

        private lateinit var current: Gallery

        fun suspendGallery() {

            if(!current.job.isCancelled) {

                current.job.cancel()
                MainActivity.showPauseIcon()

            }

        }

        fun startGallery() {

            if(current.job.isCancelled) {

                current.startGalleryThread()

                GlobalScope.launch(Dispatchers.Main) {
                    MainActivity.hidePauseIcon()
                }

            }

        }

        fun flipGallery(flipDirection: FlipDirection) {

            suspendGallery()
            current.prepAndFlip(flipDirection)
            MainActivity.cycleBackgroundColor()

        }

        fun isSuspended(): Boolean = current.job.isCancelled

    }

    init {

        Gallery.current = this

        slideLeftTranslateCurrent.fillAfter = true
        slideLeftTranslateNext.fillAfter = true
        slideRightTranslateCurrent.fillAfter = true
        slideRightTranslateNext.fillAfter = true

        val listener = object : Animation.AnimationListener {

            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {

                transitionActive = false

            }

        }

        slideLeftTranslateNext.setAnimationListener(listener)
        slideRightTranslateNext.setAnimationListener(listener)
        fadeOutCurrent.setAnimationListener(listener)

        fadeOutCurrent.fillAfter = true
        fadeInNext.fillAfter = true

        startGalleryThread()

    }

    private fun startGalleryThread() {

        job = GlobalScope.launch(Dispatchers.IO) {

            while(isActive) {

                GlobalScope.launch(Dispatchers.Main) {

                    prepAndFlip(FlipDirection.LEFT)

                }

                Thread.sleep((UserOptions.timePerPhoto * 1000).toLong())

            }

        }

    }

    private fun prepAndFlip(flipDirection: FlipDirection) {

        if(!transitionActive && ImageLoader.nextLoaded) {

            val imViewCurrent = imageViews[current]
            val imViewNext = imageViews[1 - current]

            val image =
                    if (flipDirection == FlipDirection.LEFT) ImageLoader.next()
                    else ImageLoader.previous()

            when (UserOptions.transitionType) {

                TransitionType.FADE -> fade(image, imViewCurrent, imViewNext)
                TransitionType.SLIDE -> slide(image, imViewCurrent, imViewNext, flipDirection)
                else -> cut(image, imViewCurrent, imViewNext)

            }

            current = 1 - current

        }

    }

    private fun cut(image: Bitmap?, current: ImageView, next: ImageView) {

        next.setImageBitmap(image)

        current.alpha = 0f
        next.alpha = 1f

    }

    private fun slide(image: Bitmap?, current: ImageView, next: ImageView, flipDirection: FlipDirection) {

        transitionActive = true

        current.animation = null
        next.animation = null

        current.alpha = 1f
        next.alpha = 1f

        next.setImageBitmap(image)

        slideLeftTranslateNext.duration = UserOptions.transitionSpeed.speed
        slideLeftTranslateCurrent.duration = UserOptions.transitionSpeed.speed
        slideRightTranslateNext.duration = UserOptions.transitionSpeed.speed
        slideRightTranslateCurrent.duration = UserOptions.transitionSpeed.speed

        if(flipDirection == FlipDirection.LEFT) {

            current.startAnimation(slideLeftTranslateCurrent)
            next.startAnimation(slideLeftTranslateNext)

        }

        else {

            current.startAnimation(slideRightTranslateCurrent)
            next.startAnimation(slideRightTranslateNext)

        }

    }

    private fun fade(image: Bitmap?, current: ImageView, next: ImageView) {

        transitionActive = true

        current.animation = null
        next.animation = null

        current.alpha = 1f
        next.alpha = 1f

        next.setImageBitmap(image)

        fadeInNext.duration = UserOptions.transitionSpeed.speed
        fadeOutCurrent.duration = UserOptions.transitionSpeed.speed

        next.startAnimation(fadeInNext)
        current.startAnimation(fadeOutCurrent)

    }

}