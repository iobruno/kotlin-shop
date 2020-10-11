package io.petproject.model

data class Address(
    val country: String,
    val streetAddress: String,
    val zipCode: String,
    val city: String,
    val state: String
) {

    companion object {
        val builder = Builder()
    }

    init {
        require(country.isNotEmpty()) { "Country cannot be empty" }
        require(streetAddress.isNotEmpty()) { "Street address cannot be empty" }
        require(zipCode.isNotEmpty()) { "Postal code cannot be empty" }
        require(city.isNotEmpty()) { "City cannot be empty" }
        require(state.isNotEmpty()) { "State cannot be empty" }
    }

    class Builder {
        private lateinit var country: String
        private lateinit var streetAddress: String
        private lateinit var zipCode: String
        private lateinit var city: String
        private lateinit var state: String

        fun withCountry(country: String) = apply {
            this.country = country.trim()
        }

        fun withStreetAddress(streetAddress: String): Builder = apply {
            this.streetAddress = streetAddress.trim()
        }

        fun withZipCode(zipCode: String) = apply {
            this.zipCode = zipCode.trim()
        }

        fun withCity(city: String) = apply {
            this.city = city.trim()
        }

        fun withState(state: String) = apply {
            this.state = state.trim()
        }

        fun build() = Address(country, streetAddress, zipCode, city, state)
    }
}
