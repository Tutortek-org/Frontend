package com.tutortekorg.tutortek.navigation_fragments.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.tutortekorg.tutortek.databinding.FragmentProfilePhotoBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ProfilePhotoFragment : Fragment() {
    private lateinit var binding: FragmentProfilePhotoBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var currentPhotoPath: String
    private lateinit var photoURI: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilePhotoBinding.inflate(inflater, container, false)
        binding.btnTakePhoto.setOnClickListener { dispatchTakePictureIntent() }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val matrix = getOrientationMatrix()
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                binding.profilePhoto.setImageBitmap(rotatedBitmap)
                //binding.profilePhoto.setImageBitmap(it.data?.extras?.get(MediaStore.EXTRA_OUTPUT) as Bitmap)
            }
        }
    }

    private fun getOrientationMatrix(): Matrix {
        val exif = ExifInterface(currentPhotoPath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
        val matrix = Matrix()
        println("LOOOOOOOOOL $orientation")
        val degrees = when(orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90F
            ExifInterface.ORIENTATION_ROTATE_180 -> 180F
            ExifInterface.ORIENTATION_ROTATE_270 -> 270F
            else -> 0F
        }
        println("LMAOOOOOO $degrees")
        matrix.postRotate(degrees)
        return matrix
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
        activityResultLauncher.launch(intent)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
}
