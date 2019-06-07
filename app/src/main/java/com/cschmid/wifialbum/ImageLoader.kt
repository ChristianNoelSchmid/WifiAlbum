package com.cschmid.wifialbum

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.support.media.ExifInterface
import android.util.Log
import com.cschmid.wifialbum.Activities.MainActivity
import java.io.*
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

data class Image(
        val filename: String,
        val image: Bitmap
)

// The ImageLoader, which contains a list of images,
// and the associated functions concerning adding images,
// removing images, etc.
class ImageLoader {

    companion object {

        private var counter = -1

        private var previous: Image? = null
        private var current: Image? = null
        private var next: Image? = null

        private fun screenWidth() = Resources.getSystem().displayMetrics.widthPixels
        private fun screenHeight() = Resources.getSystem().displayMetrics.heightPixels

        private val imgDirectory = "${MainActivity.getContext().filesDir}/img"

        var nextLoaded = true

        init {

            val directory = File(imgDirectory)

            if(!directory.exists())
                directory.mkdirs()

        }

        // Loads the next image in the img directory of the app
        private fun loadImage(nextOrPrevious: Boolean): Image? {

            // First, create a list of the files
            val files = File(imgDirectory).listFiles()

            // if list is empty, there are no images: return null
            if(files.isEmpty()) return null

            // Set the chosen file to default index 0
            var chosen: File = files[0]

            // Cycle through files. If current is found,
            // set next image to image after it
            for(index in 0 until files.size) {

                if(files[index].name == current?.filename) {

                    if(nextOrPrevious) {

                        chosen = if (index == files.size - 1)
                            files[0]
                        else
                            files[index + 1]

                    }

                    else {

                        chosen = if (index == 0)
                            files[files.size - 1]
                        else
                            files[index - 1]

                    }

                }

            }

            // Return newly created Image
            return try {

                val bitmap = BitmapFactory.decodeStream(chosen.inputStream())
                chosen.inputStream().close()

                if(bitmap != null)
                    Image(chosen.name, bitmap)
                else return null

            } catch(iae: IllegalArgumentException) {

                Log.e("ErrorIS", "Input Stream was not valid")
                null

            } catch (npe: NullPointerException) {

                Log.e("ErrorIS", "Input Stream is null")
                null

            }

        }

        // Create and save new (scaled) image to internal storage
        fun createImage(inputStream: InputStream, filename: String) {

            val bitmap: Bitmap

            try {

                bitmap = Bitmap.createBitmap(BitmapFactory.decodeStream(inputStream))
                inputStream.close()

            } catch(iae: IllegalArgumentException) {

                Log.e("ErrorIS", "Input Stream was not valid")
                return

            }

            catch (npe: NullPointerException) {

                Log.e("ErrorIS", "Input Stream is null")
                return

            }

            val maxXPer: Float = if(screenWidth() > bitmap.width) 1.0f else screenWidth() / bitmap.width.toFloat()
            val maxYPer: Float = if(screenHeight() > bitmap.height) 1.0f else screenHeight() / bitmap.height.toFloat()

            val resizeRatio = if(maxXPer < maxYPer) maxXPer else maxYPer

            val image = Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * resizeRatio).toInt(),
                    (bitmap.height * resizeRatio).toInt(),
                    true
            )

            saveImage(image, filename)

        }

        // Save the image to internal storage
        private fun saveImage(image: Bitmap, filename: String) {

            FileOutputStream("$imgDirectory/$filename").use {

                try {

                    image.compress(Bitmap.CompressFormat.PNG, 100, it)

                }

                catch (ioe: IOException) {

                    ioe.printStackTrace()

                }


            }

        }

        // Check if image exists in internal storage
        fun imageExists(filename: String): Boolean {

            val file = File("${imgDirectory}/$filename")
            return file.exists()

        }

        // Cycles through images with a list of Strings
        // If there are not strings that match the image name,
        // Remove the image from internal storage
        fun removeImages(imageList: ArrayList<String>) {

            val directory = File(imgDirectory)

            for(filename in directory.list()) {

                var found = false
                for (name in imageList) {

                    if(filename == name) {

                        found = true
                        break

                    }

                }

                if(!found)
                    removeFromResources(filename)

            }

        }

        // Remove an image from internal storage
        private fun removeFromResources(filename: String) {

            val file = File("${imgDirectory}/$filename")
            if(file.exists()) file.delete()

        }

        // Cycle through images in internal storage forward
        fun next(): Bitmap? {

            nextLoaded = false

            previous = current
            current = next

            next = loadImage(true)

            nextLoaded = true

            return current?.image

        }

        // Cycle through images in internal storage backwards
        fun previous(): Bitmap? {

            nextLoaded = false

            next = current
            current = previous

            previous = loadImage(false)

            nextLoaded = true

            return current?.image

        }

    }

}