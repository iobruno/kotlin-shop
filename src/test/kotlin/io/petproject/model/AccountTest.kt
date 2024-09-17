package io.petproject.model

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class AccountTest {

    @Test
    fun `when name is blank, throw IllegalArgEx`() {
        shouldThrowExactly<IllegalArgumentException> { Account("", "john.doe@domain.suffix") }
            .message shouldBe "Name cannot be blank"
    }

    @Test
    fun `when email is invalid, throw IllegalArgEx`() {
        shouldThrowExactly<IllegalArgumentException> { Account("Bruno", "invalidEmail") }
            .message shouldBe "Invalid email address"
    }
}
