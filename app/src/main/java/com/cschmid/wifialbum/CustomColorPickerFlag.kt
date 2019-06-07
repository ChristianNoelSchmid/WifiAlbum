package com.cschmid.wifialbum

import android.content.Context
import android.view.View
import android.widget.TextView
import com.skydoves.colorpickerpreference.ColorEnvelope
import com.skydoves.colorpickerpreference.FlagView

class CustomColorPickerFlag(context: Context, layout: Int) : FlagView(context, layout) {

    private val view: View

    init {
        view = findViewById<View>(R.id.flag_color_layout)
    }

    override fun onRefresh(colorEnvelope: ColorEnvelope?) {

        view.setBackgroundColor(colorEnvelope!!.color)

    }

}