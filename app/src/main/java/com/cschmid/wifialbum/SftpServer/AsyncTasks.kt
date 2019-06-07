package com.cschmid.wifialbum.SftpServer

import android.media.MediaPlayer
import com.cschmid.wifialbum.Activities.MainActivity
import com.cschmid.wifialbum.Data.UserOptions
import com.cschmid.wifialbum.ImageLoader
import com.jcraft.jsch.*
import com.cschmid.wifialbum.Data.ServerMessage
import com.cschmid.wifialbum.R

import kotlinx.coroutines.*
import java.util.*

class Server {

    companion object {

        var serverMessage: ServerMessage = ServerMessage.CANNOT_CONNECT
            private set

        private var suspended = false

        private var albumChannel: ChannelSftp? = null

        private var downloading = false

        private val mediaPlayer = MediaPlayer.create(MainActivity.getContext(), R.raw.little_fanfare)

        fun startServerThread() {

            GlobalScope.launch(Dispatchers.IO) {

                while(isActive) {

                    Thread.sleep(10000)

                    if(suspended) continue

                    when(serverMessage) {

                        ServerMessage.CANNOT_CONNECT -> {

                            val channel = connectToSftpServer(/*...*/)

                            if (channel != null) {

                                getIpFromServer(channel)
                                serverMessage = ServerMessage.CONNECTING

                            }

                        }

                        ServerMessage.CONNECTING -> {

                            albumChannel = connectToSftpServer(/*...*/, UserOptions.serverIp)

                            if (albumChannel != null)
                                serverMessage = ServerMessage.CONNECTED
                            else
                                serverMessage = ServerMessage.CANNOT_CONNECT


                        }

                        ServerMessage.CONNECTED -> {

                            grabPhotosFromServer()

                        }

                    }

                }

            }

        }

        private fun grabPhotosFromServer() {

            if(!downloading) {

                downloading = true

                try {

                    val files: Vector<ChannelSftp.LsEntry> = albumChannel!!.ls("/${UserOptions.photoDirectoryName}/") as Vector<ChannelSftp.LsEntry>

                    val filenames: ArrayList<String> = arrayListOf()
                    for (entry in files) filenames.add(entry.filename)
                    ImageLoader.removeImages(filenames)

                    for (filename in filenames) {

                        if (filename == "." || filename == "..") continue

                        if (!ImageLoader.imageExists(filename)) {

                            ImageLoader.createImage(
                                    albumChannel!!.get("/${UserOptions.photoDirectoryName}/$filename"),
                                    filename)

                            mediaPlayer.start()

                        }

                    }



                } catch (sftpe: SftpException) {

                    sftpe.printStackTrace()
                    serverMessage = ServerMessage.CANNOT_CONNECT

                } catch (npe: NullPointerException) {

                    npe.printStackTrace()
                    serverMessage = ServerMessage.CANNOT_CONNECT

                }

                downloading = false

            }

        }

        private fun getIpFromServer(channel: ChannelSftp) {

            GlobalScope.launch {

                channel.cd("pap_project")
                val reader = channel.get("pap.dat").bufferedReader()

                UserOptions.serverIp = reader.use {

                    reader.readLine()!!

                }

            }

        }

    }

}



// TODO - cns needs revamp, but achieves the purpose currently
fun connectToSftpServer(username: String, password: String, host: String): ChannelSftp? {

    val jsch = JSch()

    try {

        val session = jsch.getSession(username, host, 22)

        session.setConfig("StrictHostKeyChecking", "no")
        session.setPassword(password)

        session.connect()

        val channel = session.openChannel("sftp")

        if (channel != null) {

            channel.connect()

            return channel as ChannelSftp

        }

        else return null

    } catch (e: JSchException) {

        e.printStackTrace()
        return null

    } catch (e: SftpException) {

        e.printStackTrace()
        return null

    }

}

