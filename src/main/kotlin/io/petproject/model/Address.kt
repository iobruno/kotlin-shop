package io.petproject.model

data class Address(val country: String,
                   val streetAddress: String,
                   val zipCode: String,
                   val city: String,
                   val state: String) {

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

        fun country(country: String) = apply { this.country = country.trim() }

        fun streetAddress(streetAddress: String) = apply { this.streetAddress = streetAddress.trim() }

        fun zipCode(zipCode: String) = apply { this.zipCode = zipCode.trim() }

        fun city(city: String) = apply { this.city = city.trim() }

        fun state(state: String) = apply { this.state = state.trim() }

        fun build() = Address(country, streetAddress, zipCode, city, state)
    }

}