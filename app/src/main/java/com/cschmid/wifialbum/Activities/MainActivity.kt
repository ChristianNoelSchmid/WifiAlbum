package com.cschmid.wifialbum.Activities

import android.animation.ArgbEvaluator
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.res.Configuration
import android.support.v4.view.GestureDetectorCompat
import android.view.*
import com.cschmid.wifialbum.Data.UserOptions
import com.cschmid.wifialbum.Gallery
import com.cschmid.wifialbum.R
import com.cschmid.wifialbum.SftpServer.Server
import com.cschmid.wifialbum.Data.FlipDirection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.file.attribute.UserPrincipal

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var detectorCompat: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main)
        //fullScreencall()

        //FadeAnimationTask().execute(imageView, imageView2)

        Server.startServerThread()
        Gallery(imageView, imageView2)

        detectorCompat = GestureDetectorCompat(this, this)

        cycleBackgroundColor()

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return if (detectorCompat.onTouchEvent(event))
            true

        else
            super.onTouchEvent(event)

    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent?) { }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {

        if (velocityY > -800 && velocityY < 800) {

            if (velocityX >= 40) {

                Gallery.flipGallery(FlipDirection.RIGHT)
                return true

            } else if (velocityX <= -40) {

                Gallery.flipGallery(FlipDirection.LEFT)
                return true

            }

        }

        else if(velocityY <= -800) {

            showMenu()
            return true

        }

        else {

            if(Gallery.isSuspended())
                Gallery.startGallery()

            else
                Gallery.suspendGallery()

        }

        return false

    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {

        super.onConfigurationChanged(newConfig)

        cycleBackgroundColor()

    }

    private fun showMenu() {

        val menuIntent = Intent(this, MenuActivity::class.java)
        Gallery.suspendGallery()
        startActivity(menuIntent)

    }

    companion object {

        private lateinit var current: MainActivity

        private var colorLerp = 0.0f
        private var lerpIncrementing = true
        private val argEvaluator = ArgbEvaluator()

        fun cycleBackgroundColor() {

            if (lerpIncrementing) colorLerp += 0.05f
            else colorLerp -= 0.05f

            colorLerp = Math.min(colorLerp, 1.0f)
            colorLerp = Math.max(colorLerp, 0.0f)

            if (colorLerp >= 1.0f) lerpIncrementing = false
            else if (colorLerp <= 0.0f) lerpIncrementing = true

            val color = argEvaluator.evaluate(
                    colorLerp,
                    UserOptions.backgroundColorFirst,
                    UserOptions.backgroundColorSecond
            ) as Int

            current.mainLayout.setBackgroundColor(color)

        }

        fun getContext(): Context = current.applicationContext

        fun showPauseIcon() { current.imageViewPause.alpha = 1.0f }
        fun hidePauseIcon() { current.imageViewPause.alpha = 0.0f }

    }

    init {

        MainActivity.current = this

    }

    override fun onPause() {

        Gallery.suspendGallery()

        super.onPause()

    }

    override fun onResume() {

        Gallery.startGallery()

        super.onResume()

    }

}