package com.cschmid.wifialbum.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.cschmid.wifialbum.CustomListAdapter
import com.cschmid.wifialbum.Gallery
import com.cschmid.wifialbum.R
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.coroutines.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val nameArray = arrayOf("Network Options", "Gallery Menu", "Back")
        val imageIDArray = arrayOf(R.drawable.wifi, R.drawable.settings, R.drawable.back)

        val onClickListenerArray = arrayOf(

                View.OnClickListener() {

                    openNetworkSettings()

                },

                View.OnClickListener {

                    openGallerySettings()

                },

                View.OnClickListener {

                    Gallery.startGallery()
                    finish()

                }

        )

        val customListAdapter = CustomListAdapter(this, nameArray, imageIDArray, onClickListenerArray)

        listView.adapter = customListAdapter

    }

    fun openNetworkSettings() {

        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        intent.putExtra("only_access_points", true);
        intent.putExtra("extra_prefs_show_button_bar", true);
        startActivityForResult(intent, 1);

    }

    fun openGallerySettings() {

        val intent = Intent(this, GalleryMenuActivity::class.java)
        startActivity(intent)

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
