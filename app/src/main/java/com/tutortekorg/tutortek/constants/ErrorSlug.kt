package com.tutortekorg.tutortek.constants

class ErrorSlug {
    companion object {
        const val INCORRECT_CREDENTIALS = "Incorrect email or password"
        const val LOGIN_ERROR = "Unexpected error while logging in"
        const val REGISTER_ERROR = "Unexpected error while registering"
        const val FIELD_EMPTY = "This field cannot be empty"
        const val PASSWORD_TOO_SHORT = "Password must be at least 8 characters long"
        const val INVALID_EMAIL = "Please enter a valid e-mail"
    }
}
