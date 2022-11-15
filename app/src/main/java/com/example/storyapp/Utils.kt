package com.example.storyapp

import android.animation.ObjectAnimator
import android.app.Application
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class Utils {

    companion object {
        const val BASE_URL = "https://story-api.dicoding.dev/v1/"
        private const val FILENAME_FORMAT = "dd-MMM-yyyy"
        const val BEARER = "Bearer "

        val timeStamp: String = SimpleDateFormat(
            FILENAME_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())


        fun createFile(application: Application): File {
            val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
                File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
            }

            val outputDirectory = if (
                mediaDir != null && mediaDir.exists()
            ) mediaDir else application.filesDir

            return File(outputDirectory, "$timeStamp.jpg")
        }

        fun reduceFileImage(file: File): File {
            val bitmap = BitmapFactory.decodeFile(file.path)
            var compressQuality = 100
            var streamLength: Int

            do {
                val bmpStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPicByteArray = bmpStream.toByteArray()
                streamLength = bmpPicByteArray.size
                compressQuality -= 5
            } while (streamLength > 1_000_000)

            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
            return file
        }

        fun bitmapToFile(
            file: File?,
            bitmap: Bitmap
        ): File? {
            //create a file to write bitmap data
            var compressQuality = 100
            var streamLength: Int

            do {
                val bmpStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPicByteArray = bmpStream.toByteArray()
                streamLength = bmpPicByteArray.size
                compressQuality -= 5
            } while (streamLength > 1_000_000)

            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
            return file
        }

        fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
            val matrix = Matrix()
            return if (isBackCamera) {
                matrix.postRotate(90f)
                Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    matrix,
                    true
                )
            } else {
                matrix.postRotate(-90f)
                matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
                Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    matrix,
                    true
                )
            }
        }

        fun uriToFile(selectedImg: Uri, application: Application): File {
            val contentResolver: ContentResolver = application.contentResolver
            val myFile = createFile(application)

            val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
            val outPutStream: OutputStream = FileOutputStream(myFile)
            val buf = ByteArray(1024)
            var len: Int

            while (inputStream.read(buf).also { len = it } > 0) outPutStream.write(buf, 0, len)

            outPutStream.close()
            inputStream.close()


            return myFile
        }

        fun setUpView(window: Window, supportActionBar: ActionBar?) {
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
            supportActionBar?.hide()
        }

        fun fadeInAnimation(view: View): ObjectAnimator {
            return ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(500)
        }

        infix fun View.onClick(onClickListener: View.OnClickListener) {
            setOnClickListener(onClickListener)
        }
    }
}