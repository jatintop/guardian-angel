package com.plcoding.m3_bottomnavigation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {
    private const val PROFILE_IMAGE_FILE_NAME = "profile_image.jpg"

    fun saveImageToInternalStorage(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val file = File(context.filesDir, PROFILE_IMAGE_FILE_NAME)
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun loadImageFromInternalStorage(context: Context): Bitmap? {
        val file = File(context.filesDir, PROFILE_IMAGE_FILE_NAME)
        return if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    }

    fun getImageUri(context: Context): Uri {
        val file = File(context.filesDir, PROFILE_IMAGE_FILE_NAME)
        return Uri.fromFile(file)
    }
}