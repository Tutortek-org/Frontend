package com.tutortekorg.tutortek

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.auth0.android.jwt.JWT
import com.tutortekorg.tutortek.constants.TutortekConstants
import com.tutortekorg.tutortek.data.UserProfile
import com.tutortekorg.tutortek.databinding.ActivityHomeBinding
import com.tutortekorg.tutortek.singletons.ProfileSingleton
import com.tutortekorg.tutortek.singletons.RequestSingleton
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)

        sendProfileGetRequest()
    }

    private fun sendProfileGetRequest() {
        val token = TutortekUtils.getJwtToken(this)
        val profileId = token?.let { getProfileId(it) }
        val url = "${TutortekConstants.BASE_URL}/profiles/$profileId"
        val request = object : JsonObjectRequest(
            Method.GET, url, null,
            {
                if (token != null)
                    addUserProfileBundle(it, token)
            },
            {
                Toast.makeText(this, getString(R.string.error_profile_retrieval), Toast.LENGTH_SHORT).show()
            }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        RequestSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun getProfileId(token: String): Long? {
        val jwt = JWT(token)
        val profileId = jwt.getClaim("pid")
        return profileId.asLong()
    }

    private fun getRoles(token: String): List<String> {
        val jwt = JWT(token)
        val roles = jwt.getClaim("roles")
        return roles.asList(String::class.java)
            .map { r -> r.lowercase().replaceFirstChar { it.uppercase() } }
    }

    private fun addUserProfileBundle(body: JSONObject, token: String) {
        val roles = getRoles(token)
        val profile = UserProfile(body, roles)
        ProfileSingleton.getInstance().userProfile = profile
    }
}
