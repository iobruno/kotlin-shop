package io.petproject.model

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

internal class AccountTest : AnnotationSpec() {

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
