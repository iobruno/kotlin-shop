package io.petproject.model

import io.petproject.utils.Patterns.emailRegex

data class Account(val name: String, val email: String) {

    init {
        require(name.isNotEmpty()) { "Name cannot be blank" }
        require(emailRegex.matcher(email).matches()) { "Invalid email address" }
    }
}
