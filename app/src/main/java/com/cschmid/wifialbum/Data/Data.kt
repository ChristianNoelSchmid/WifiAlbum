package com.cschmid.wifialbum.Data

import android.graphics.Color

const val DEFAULT_TIME_PER_PHOTO: Int = 10
const val DEFAULT_NETWORK_UPDATE_LENGTH = 1800

enum class MainMenuItem (
        val guiName: String
) {

    NETWORK("Network"),
    GALLERY_OPTIONS("Gallery Options");

    override fun toString(): String = guiName

}

enum class TransitionSpeed (
        val guiName: String,
        val speed: Long,
        val index: Int
) {

    VERY_SLOW("Very Slow (3 seconds)", 3000, 0),
    SLOW("Slow (2 seconds)", 2000, 1),
    MEDIUM("Medium (1.5 seconds)", 1500, 2),
    FAST("Fast (1 second)", 1000, 3),
    VERY_FAST("Very Fast (0.5 seconds)", 500, 4);

}

enum class TransitionType(guiName: String) {

    CUT("Cut"),
    SLIDE("Slide"),
    FADE("Fade")

}

enum class FlipDirection {

    LEFT,
    RIGHT

}

enum class ServerMessage (
        color: Int,
        message: String
        ) {

    CONNECTING(Color.YELLOW, "Connecting to network..."),
    CANNOT_CONNECT(Color.RED, "Cannot connect to server"),
    CONNECTED(Color.GREEN, "Connected to server")

}