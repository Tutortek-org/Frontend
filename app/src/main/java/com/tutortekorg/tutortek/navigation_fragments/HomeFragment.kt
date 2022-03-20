package com.tutortekorg.tutortek.navigation_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.android.volley.Request
import com.google.firebase.messaging.FirebaseMessaging
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.databinding.FragmentHomeBinding
import com.tutortekorg.tutortek.requests.TutortekObjectRequest
import com.tutortekorg.tutortek.requests.retrofit.FileDownloadService
import com.tutortekorg.tutortek.requests.retrofit.ServiceGenerator
import com.tutortekorg.tutortek.singletons.ProfileSingleton
import com.tutortekorg.tutortek.singletons.RequestSingleton
import com.tutortekorg.tutortek.utils.JwtUtils
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.lang.Exception

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnAllTopics.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_topicListFragment)
        }
        binding.btnMyTopics.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_personalTopicListFragment)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        downloadProfilePhoto()
        registerDeviceToken()
    }

    private fun registerDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val url = "${TutortekConstants.BASE_URL}/notifications"
                val body = JSONObject()
                body.put("deviceToken", it.result)
                val request = TutortekObjectRequest(requireContext(), Request.Method.POST, url, body, {}, {})
                RequestSingleton.getInstance(requireContext()).addToRequestQueue(request)
            }
        }
    }

    private fun downloadProfilePhoto() {
        val service = ServiceGenerator.createService(FileDownloadService::class.java)
        val token = JwtUtils.getJwtToken(requireContext())
        val call = service.downloadProfilePicture("Bearer $token")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.isSuccessful!!) savePhotoToDevice(response.body())
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {}
        })
    }

    private fun savePhotoToDevice(body: ResponseBody) {
        try {
            val filePath = context?.getExternalFilesDir(null).toString() + File.separator + "pfp"
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
                ProfileSingleton.getInstance().userProfile?.photoPath = filePath
            }
            catch (e: IOException) {
                try {
                    Toast.makeText(requireContext(), R.string.error_photo_file_create, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), R.string.error_photo_file_create, Toast.LENGTH_SHORT).show()
            }
            catch (e: Exception){}
        }
    }
}
