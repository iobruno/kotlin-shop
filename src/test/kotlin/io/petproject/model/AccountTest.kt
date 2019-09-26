package io.petproject.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AccountTest {

    @Test
    fun `when name is blank, throw IllegalArgEx`() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            Account("", "john.doe@domain.suffix")
        }
        assertThat(ex.message).isEqualTo("Name cannot be blank")
    }

    @Test
    fun `when email is invalid, throw IllegalArgEx`() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            Account("Bruno", "invalidEmail")
        }
        assertThat(ex.message).isEqualTo("Invalid email address")
    }

}