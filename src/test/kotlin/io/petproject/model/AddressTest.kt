package io.petproject.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class AddressTest {

    @Test
    fun `should build a valid address`() {
        val address = Address.builder
            .withCountry("Brazil ")
            .withStreetAddress("Av Paulista, 1000 ")
            .withZipCode("01000-000")
            .withState("SP ")
            .withCity(" Sao Paulo ")
            .build()

        assertThat(address.city).isEqualTo("Sao Paulo")
        assertThat(address.state).isEqualTo("SP")
        assertThat(address.country).isEqualTo("Brazil")
        assertThat(address.streetAddress).isEqualTo("Av Paulista, 1000")
        assertThat(address.zipCode).isEqualTo("01000-000")
    }

    @Test
    fun `when country is blank, throw IllegalArgEx`() {
        assertThatThrownBy {
            Address.builder
                .withCountry("")
                .withStreetAddress("Av Paulista, 1000 ")
                .withZipCode(" 01000-000")
                .withState("SP ")
                .withCity(" Sao Paulo ")
                .build()
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Country cannot be empty")
    }

    @Test
    fun `when streetName is blank, throw IllegalArgEx`() {
        assertThatThrownBy {
            Address.builder
                .withCountry("Brazil ")
                .withStreetAddress("")
                .withZipCode(" 01000-000")
                .withState("SP ")
                .withCity(" Sao Paulo ")
                .build()
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Street address cannot be empty")
    }

    @Test
    fun `when postalCode is blank, throw IllegalArgEx`() {
        assertThatThrownBy {
            Address.builder
                .withCountry("Brazil ")
                .withStreetAddress("Av Paulista, 1000 ")
                .withZipCode("")
                .withState("SP ")
                .withCity(" Sao Paulo ")
                .build()
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Postal code cannot be empty")
    }

    @Test
    fun `when state is blank, throw IllegalArgEx`() {
        assertThatThrownBy {
            Address.builder
                .withCountry("Brazil ")
                .withStreetAddress("Av Paulista, 1000 ")
                .withZipCode(" 01000-000")
                .withState("")
                .withCity(" Sao Paulo ")
                .build()
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("State cannot be empty")
    }

    @Test
    fun `when city is blank, throw IllegalArgEx`() {
        assertThatThrownBy {
            Address.builder
                .withCountry("Brazil ")
                .withStreetAddress("Av Paulista, 1000 ")
                .withZipCode(" 01000-000")
                .withCity("")
                .withState("SP ")
                .build()
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("City cannot be empty")
    }
}
