package io.petproject.model

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class AccountTest {

    @Test
    fun `when name is blank, throw IllegalArgEx`() {
        assertThatThrownBy { Account("", "john.doe@domain.suffix") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Name cannot be blank")
    }

    @Test
    fun `when email is invalid, throw IllegalArgEx`() {
        assertThatThrownBy { Account("Bruno", "invalidEmail") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid email address")
    }
}
