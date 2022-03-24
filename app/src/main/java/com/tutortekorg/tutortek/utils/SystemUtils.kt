package com.tutortekorg.tutortek.utils

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.squareup.picasso.Picasso
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.UserProfile
import com.tutortekorg.tutortek.requests.retrofit.FileDownloadService
import com.tutortekorg.tutortek.requests.retrofit.ServiceGenerator
import com.tutortekorg.tutortek.singletons.ProfileSingleton
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.lang.Exception

object SystemUtils {
    fun showConfirmDeleteDialog(
        context: Context,
        messageID: Int,
        name: String,
        requestFunction: () -> Unit
    ) {
        val message = context.getString(messageID, name)
        val alert = AlertDialog.Builder(context)
            .setTitle(R.string.confirm_delete)
            .setMessage(message)
            .setPositiveButton(R.string.btn_no) { _: DialogInterface, _: Int -> }
            .setNegativeButton(R.string.btn_yes) { _: DialogInterface, _: Int ->
                requestFunction()
            }
            .create()
        val color = context.getColor(R.color.color_primary)
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color)
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color)
    }

    fun downloadProfilePhoto(context: Context, userProfile: UserProfile?, imageView: CircleImageView? = null) {
        val service = ServiceGenerator.createService(FileDownloadService::class.java)
        val token = JwtUtils.getJwtToken(context)
        val call = userProfile?.id?.let { service.downloadProfilePicture("Bearer $token", it) }
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.isSuccessful!!) savePhotoToDevice(context, response.body(), userProfile, imageView)
            }
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {}
        })
    }

    private fun savePhotoToDevice(context: Context, body: ResponseBody, userProfile: UserProfile?, imageView: CircleImageView?) {
        try {
            val filePath = context.getExternalFilesDir(null).toString() + File.separator + "pfp${userProfile?.id}.png"
            val file = File(filePath)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)

                while (true) {
                    val readResult = inputStream.read(fileReader)
                    if(readResult == -1) break
                    outputStream.write(fileReader, 0, readResult)
                }

                outputStream.flush()
                if(imageView != null) {
                    val picasso = Picasso.get()
                    picasso.invalidate("file://$filePath")
                    picasso.load("file://$filePath")
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .fit()
                        .into(imageView)
                }
                else ProfileSingleton.getInstance().userProfile?.photoPath = filePath
            }
            catch (e: IOException) {
                try {
                    Toast.makeText(context, R.string.error_photo_file_create, Toast.LENGTH_SHORT).show()
                }
                catch (e: Exception){}
            }
            finally {
                inputStream?.close()
                outputStream?.close()
            }
        }
        catch (e: IOException) {
            try {
                Toast.makeText(context, R.string.error_photo_file_create, Toast.LENGTH_SHORT).show()
            }
            catch (e: Exception){}
        }
    }
}
