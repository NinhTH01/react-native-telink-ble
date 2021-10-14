package com.react.telink.ble

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import com.react.telink.ble.TelinkBleApplication.Companion.getInstance
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

class AppCrashHandler private constructor(private val context: Context) :
  Thread.UncaughtExceptionHandler {

  private val handler: Thread.UncaughtExceptionHandler? =
    Thread.getDefaultUncaughtExceptionHandler()

  @SuppressLint("SimpleDateFormat")
  override fun uncaughtException(thread: Thread, throwable: Throwable) {
    val simpleDateFormat = SimpleDateFormat(
      "dd-MM-yyyy hh:mm:ss"
    )
    val buff = StringBuilder()
    buff
      .append("Date: ")
      .append(simpleDateFormat.format(Date()))
      .append("\n")
      .append("========MODEL:").append(Build.MODEL).append(" \n")

    // stack info
    buff.append("Stacktrace:\n\n")
    val stringWriter = StringWriter()
    val printWriter = PrintWriter(stringWriter)
    throwable.printStackTrace(printWriter)
    buff
      .append(stringWriter.toString())
      .append("===========\n")
    printWriter.close()
    write2ErrorLog(buff.toString())
    handler?.uncaughtException(thread, throwable)
  }

  /**
   * 创建总文件夹
   */
  private val filePath: String
    get() {
      val cachePath: String = if (Environment.getExternalStorageState() ==
        Environment.MEDIA_MOUNTED
      ) {
        context.getExternalFilesDir("TELINK_CACHE").toString() + DISK_CACHE_PATH
      } else {
        getInstance().cacheDir.toString() + DISK_CACHE_PATH
      }
      val file = File(cachePath)
      if (!file.exists()) {
        file.mkdirs()
      }
      return cachePath
    }

  private fun write2ErrorLog(content: String) {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd-hhmmss", Locale.ENGLISH)
    val fileName = "/Crash_" + simpleDateFormat.format(Date()) + ".txt"
    val file = File(filePath + fileName)
    var fos: FileOutputStream? = null
    try {
      if (file.exists()) {
        file.delete() //
      } else {
        file.parentFile!!.mkdirs()
      }
      if (file.createNewFile()) {
        fos = FileOutputStream(file)
        fos.write(content.toByteArray())
      }
    } catch (e: Exception) {
      e.printStackTrace()
    } finally {
      try {
        fos?.close()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  companion object {
    private const val DISK_CACHE_PATH = "/TelinkBleMeshCrash/"

    @SuppressLint("StaticFieldLeak")
    private var crashHandler: AppCrashHandler? = null
    fun init(context: Context): AppCrashHandler? {
      if (crashHandler == null) {
        crashHandler = AppCrashHandler(context)
      }
      return crashHandler
    }
  }

  init {
    Thread.setDefaultUncaughtExceptionHandler(this)
  }
}
