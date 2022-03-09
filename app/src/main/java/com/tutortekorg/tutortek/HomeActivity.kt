package com.tutortekorg.tutortek

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.forEach
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.tutortekorg.tutortek.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)

        binding.bottomNavigationView.setOnItemSelectedListener {
            val menu = binding.bottomNavigationView.menu

            menu.forEach { mi ->
                mi.isChecked = mi.itemId == it.itemId
            }

            val navBuilder = NavOptions.Builder()
            navBuilder
                .setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .setPopEnterAnim(android.R.anim.fade_in)
                .setPopExitAnim(android.R.anim.fade_out)
            navHostFragment.navController.popBackStack(R.id.homeFragment, false)
            navHostFragment.navController.navigate(it.itemId, null, navBuilder.build())

            return@setOnItemSelectedListener true
        }
    }
}
