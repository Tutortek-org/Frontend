package com.tutortekorg.tutortek.singletons

import com.tutortekorg.tutortek.data.UserProfile

class ProfileSingleton {
    companion object {

        @Volatile
        private var INSTANCE: ProfileSingleton? = null

        fun getInstance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProfileSingleton().also {
                    INSTANCE = it
                }
            }
    }

    var userProfile: UserProfile? = null
}
