package com.cschmid.wifialbum.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.cschmid.wifialbum.Data.UserOptions
import com.cschmid.wifialbum.Gallery
import com.cschmid.wifialbum.R
import kotlinx.android.synthetic.main.activity_directory.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class DirectoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_directory)

        editTextDirectory.setText(UserOptions.photoDirectoryName)

        imageButtonDirectoryBack.setOnClickListener {

            UserOptions.photoDirectoryName = editTextDirectory.text.toString()
            UserOptions.saveUserSettings()
            finish()

        }

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
