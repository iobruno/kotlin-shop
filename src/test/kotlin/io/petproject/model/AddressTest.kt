package io.petproject.model

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe


internal class AddressTest : AnnotationSpec() {

    @Test
    fun `should build a valid address`() {
        val address = Address.builder
            .withCountry("Brazil ")
            .withStreetAddress("Av Paulista, 1000 ")
            .withZipCode("01000-000")
            .withState("SP ")
            .withCity(" Sao Paulo ")
            .build()

        address.city shouldBe "Sao Paulo"
        address.state shouldBe "SP"
        address.country shouldBe "Brazil"
        address.streetAddress shouldBe "Av Paulista, 1000"
        address.zipCode shouldBe "01000-000"
    }

    @Test
    fun `when country is blank, throw IllegalArgEx`() {
        shouldThrowExactly<IllegalArgumentException> {
            Address.builder
                .withCountry("")
                .withStreetAddress("Av Paulista, 1000 ")
                .withZipCode(" 01000-000")
                .withState("SP ")
                .withCity(" Sao Paulo ")
                .build()
        }.message shouldBe "Country cannot be empty"
    }

    @Test
    fun `when streetName is blank, throw IllegalArgEx`() {
        shouldThrowExactly<IllegalArgumentException> {
            Address.builder
                .withCountry("Brazil ")
                .withStreetAddress("")
                .withZipCode(" 01000-000")
                .withState("SP ")
                .withCity(" Sao Paulo ")
                .build()
        }.message shouldBe "Street address cannot be empty"
    }

    @Test
    fun `when postalCode is blank, throw IllegalArgEx`() {
        shouldThrowExactly<IllegalArgumentException> {
            Address.builder
                .withCountry("Brazil ")
                .withStreetAddress("Av Paulista, 1000 ")
                .withZipCode("")
                .withState("SP ")
                .withCity(" Sao Paulo ")
                .build()
        }.message shouldBe "Postal code cannot be empty"
    }

    @Test
    fun `when state is blank, throw IllegalArgEx`() {
        shouldThrowExactly<IllegalArgumentException> {
            Address.builder
                .withCountry("Brazil ")
                .withStreetAddress("Av Paulista, 1000 ")
                .withZipCode(" 01000-000")
                .withState("")
                .withCity(" Sao Paulo ")
                .build()
        }.message shouldBe "State cannot be empty"
    }

    @Test
    fun `when city is blank, throw IllegalArgEx`() {
        shouldThrowExactly<IllegalArgumentException> {
            Address.builder
                .withCountry("Brazil ")
                .withStreetAddress("Av Paulista, 1000 ")
                .withZipCode(" 01000-000")
                .withCity("")
                .withState("SP ")
                .build()
        }.message shouldBe "City cannot be empty"
    }
}
