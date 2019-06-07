package com.cschmid.wifialbum.Activities

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar

import kotlinx.coroutines.*
import kotlinx.android.synthetic.main.activity_gallery_menu.*

import com.skydoves.colorpickerpreference.FlagMode

import com.cschmid.wifialbum.CustomColorPickerFlag
import com.cschmid.wifialbum.Data.UserOptions
import com.cschmid.wifialbum.R
import com.cschmid.wifialbum.Data.TransitionSpeed
import com.cschmid.wifialbum.Data.TransitionType
import com.cschmid.wifialbum.Gallery


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class GalleryMenuActivity : AppCompatActivity() {

    var colorTapCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_menu)

        colorPickerViewFirst.flagView = CustomColorPickerFlag(this, R.layout.custom_color_picker_flag)
        colorPickerViewFirst.flagMode = FlagMode.ALWAYS; // showing always flagView
        colorPickerViewFirst.flagMode = FlagMode.LAST; // showing flagView when touch Action_UP
        colorPickerViewFirst.preferenceName = "colorPickerFirst"

        colorPickerViewSecond.flagView = CustomColorPickerFlag(this, R.layout.custom_color_picker_flag)
        colorPickerViewSecond.flagMode = FlagMode.ALWAYS; // showing always flagView
        colorPickerViewSecond.flagMode = FlagMode.LAST; // showing flagView when touch Action_UP
        colorPickerViewSecond.preferenceName = "colorPickerSecond"

        seekBarTimePerPhoto.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewTimePerPhoto.text = (progress + 5).toString()
            }

        })

        imageButtonBack.setOnClickListener {

            saveOptions()
            finish()

        }

        importOptions()

        textViewColor.setOnClickListener {

            GlobalScope.launch {

                colorTapCount++

                if(colorTapCount == 5) {

                    val intent = Intent(this@GalleryMenuActivity, DirectoryActivity::class.java)
                    startActivity(intent)

                }

                else {

                    val threadCount = colorTapCount
                    var counter = 0

                    while (counter < 1000) {

                        if (threadCount != colorTapCount) return@launch

                        counter += 100
                        Thread.sleep(100)

                    }

                    colorTapCount = 0

                }

            }

        }

    }

    private fun importOptions() {

        colorPickerViewFirst.getSavedColor(Color.LTGRAY)
        colorPickerViewSecond.getSavedColor(Color.BLUE)
        seekBarTimePerPhoto.progress = UserOptions.timePerPhoto - 5
        textViewTimePerPhoto.text = UserOptions.timePerPhoto.toString()

        radGroupType.check( when(UserOptions.transitionType) {

            TransitionType.CUT -> radioButtonCut.id
            TransitionType.FADE -> radioButtonFade.id
            TransitionType.SLIDE -> radioButtonSlide.id

        })

        radGroupSpeed.check( when(UserOptions.transitionSpeed) {

            TransitionSpeed.VERY_FAST -> radioButtonVFast.id
            TransitionSpeed.FAST -> radioButtonFast.id
            TransitionSpeed.MEDIUM -> radioButtonMed.id
            TransitionSpeed.SLOW -> radioButtonSlow.id
            TransitionSpeed.VERY_SLOW -> radioButtonVSlow.id

        })

    }

    private fun saveOptions() {

        colorPickerViewFirst.saveData()
        colorPickerViewSecond.saveData()
        UserOptions.backgroundColorFirst = colorPickerViewFirst.color
        UserOptions.backgroundColorSecond = colorPickerViewSecond.color
        UserOptions.timePerPhoto = seekBarTimePerPhoto.progress + 5

        UserOptions.transitionType = when(radGroupType.checkedRadioButtonId) {

            radioButtonCut.id -> TransitionType.CUT
            radioButtonFade.id -> TransitionType.FADE
            else -> TransitionType.SLIDE

        }

        UserOptions.transitionSpeed = when(radGroupSpeed.checkedRadioButtonId) {

            radioButtonVSlow.id -> TransitionSpeed.VERY_SLOW
            radioButtonSlow.id -> TransitionSpeed.SLOW
            radioButtonMed.id -> TransitionSpeed.MEDIUM
            radioButtonFast.id -> TransitionSpeed.FAST
            else -> TransitionSpeed.VERY_FAST

        }

        MainActivity.cycleBackgroundColor()
        UserOptions.saveUserSettings()

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
