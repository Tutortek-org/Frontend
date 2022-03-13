package com.tutortekorg.tutortek.navigation_fragments.profile

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.databinding.FragmentProfilePhotoBinding
import com.tutortekorg.tutortek.requests.retrofit.FileUploadService
import com.tutortekorg.tutortek.requests.retrofit.ServiceGenerator
import com.tutortekorg.tutortek.utils.JwtUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ProfilePhotoFragment : Fragment() {
    private lateinit var binding: FragmentProfilePhotoBinding
    private lateinit var cameraActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var photoURI: Uri
    private lateinit var callback: Callback<ResponseBody>
    private lateinit var request: Call<ResponseBody>
    private var currentPhotoPath = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilePhotoBinding.inflate(inflater, container, false)
        binding.btnTakePhoto.setOnClickListener { dispatchTakePictureIntent() }
        binding.btnGallery.setOnClickListener { dispatchGalleryPictureIntent() }
        binding.btnUpload.setOnClickListener { uploadPhoto() }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val matrix = getOrientationMatrix()
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                binding.profilePhoto.setImageBitmap(rotatedBitmap)
            }
        }
        galleryActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                photoURI = it.data?.data!!
                binding.profilePhoto.setImageURI(photoURI)
            }
        }
    }

    private fun uploadPhoto() {
        if(!this::photoURI.isInitialized) return
        setButtonStates(false)
        val service = ServiceGenerator.createService(FileUploadService::class.java)
        val path = createCopyAndReturnRealPath(requireContext(), photoURI)
        val file = File(path!!)
        val requestFile = RequestBody.create(
            MediaType.parse(requireContext().contentResolver.getType(photoURI)),
            file
        )
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        val token = JwtUtils.getJwtToken(requireContext())
        callback = object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>?,
                response: Response<ResponseBody>?
            ) {
                when {
                    response?.isSuccessful!! -> {
                        try {
                            Toast.makeText(requireContext(), R.string.photo_upload_success, Toast.LENGTH_LONG).show()
                            findNavController().popBackStack()
                        } catch (e: Exception) {}
                    }
                    response.code() == 401 -> {
                        JwtUtils.sendRefreshRequest<JSONObject>(activity!!, false, null)
                        val refreshedToken = JwtUtils.getJwtToken(requireContext())
                        request = service.upload(body, "Bearer $refreshedToken")
                        request.enqueue(callback)
                    }
                    else -> {
                        setButtonStates(true)
                        Toast.makeText(requireContext(), R.string.error_photo_upload, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                setButtonStates(true)
                Toast.makeText(requireContext(), R.string.error_photo_upload, Toast.LENGTH_LONG).show()
            }
        }
        request = service.upload(body, "Bearer $token")
        request.enqueue(callback)
    }

    private fun setButtonStates(isActive: Boolean) {
        if(isActive) binding.btnUpload.revertAnimation()
        else binding.btnUpload.startAnimation()
        binding.btnGallery.isEnabled = isActive
        binding.btnGallery.isClickable = isActive
        binding.btnTakePhoto.isEnabled = isActive
        binding.btnTakePhoto.isClickable = isActive
    }

    private fun createCopyAndReturnRealPath(context: Context, uri: Uri): String? {
        val contentResolver: ContentResolver = context.contentResolver ?: return null
        val filePath: String = (context.applicationInfo.dataDir.toString() + File.separator + System.currentTimeMillis())
        val file = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (ignore: IOException) {
            return null
        }
        return file.absolutePath
    }

    private fun getOrientationMatrix(): Matrix {
        val exif = ExifInterface(currentPhotoPath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
        val matrix = Matrix()
        val degrees = when(orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90F
            ExifInterface.ORIENTATION_ROTATE_180 -> 180F
            ExifInterface.ORIENTATION_ROTATE_270 -> 270F
            else -> 0F
        }
        matrix.postRotate(degrees)
        return matrix
    }

    private fun dispatchGalleryPictureIntent() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private fun dispatchTakePictureIntent() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }
        photoFile?.also {
            photoURI = FileProvider.getUriForFile(requireContext(), "com.tutortekorg.tutortek.fileprovider", it)
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        cameraActivityResultLauncher.launch(intent)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
}
