package com.cschmid.wifialbum.Data

import java.io.*
import java.util.*

import android.content.Context
import android.graphics.Color

import com.cschmid.wifialbum.Activities.MainActivity

class UserOptions {

    companion object {

        var timePerPhoto: Int = DEFAULT_TIME_PER_PHOTO

        var transitionType: TransitionType = TransitionType.CUT
        var transitionSpeed: TransitionSpeed = TransitionSpeed.MEDIUM

        var backgroundColorFirst: Int = Color.LTGRAY
        var backgroundColorSecond: Int = Color.BLUE

        var photoDirectoryName: String = ""

        var serverIp: String = ""

        init {

            val file = File("${MainActivity.getContext().filesDir}/UserOptions.dat")

            if(file.exists()) {

                val settings = loadUserSettings(file)

                if(settings.containsKey("timePerPhoto")) timePerPhoto = settings["timePerPhoto"]!!.toInt()
                if(settings.containsKey("transitionType")) transitionType = TransitionType.valueOf(settings["transitionType"]!!)
                if(settings.containsKey("transitionSpeed")) transitionSpeed = TransitionSpeed.valueOf(settings["transitionSpeed"]!!)
                if(settings.containsKey("backgroundColorFirst")) backgroundColorFirst = settings["backgroundColorFirst"]!!.toInt()
                if(settings.containsKey("backgroundColorSecond")) backgroundColorSecond = settings["backgroundColorSecond"]!!.toInt()
                if(settings.containsKey("photoDirectory")) photoDirectoryName = settings["photoDirectory"]!!

            }

        }

        private fun loadUserSettings(file: File): HashMap<String, String> {

            val settings = hashMapOf<String, String>()

            file.bufferedReader().use {

                for (line in it.lineSequence()) {

                    if (!line.contains(">>"))
                        continue

                    settings[line.substringBefore(">>")] = line.substringAfter(">>")
                }

            }

            return settings

        }

        fun saveUserSettings() {

            val file = File("UserOptions.dat")
            MainActivity.getContext().openFileOutput(file.name, Context.MODE_PRIVATE).use {

                it.write("timePerPhoto>>$timePerPhoto\n".toByteArray())
                it.write("transitionType>>$transitionType\n".toByteArray())
                it.write("transitionSpeed>>$transitionSpeed\n".toByteArray())
                it.write("backgroundColorFirst>>$backgroundColorFirst\n".toByteArray())
                it.write("backgroundColorSecond>>$backgroundColorSecond\n".toByteArray())
                it.write("photoDirectory>>$photoDirectoryName".toByteArray())

            }

        }

    }

}

