package io.petproject.model

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class ProductTest {

    @Test
    fun `when name is blank, throw IllegalArgEx`() {
        shouldThrowExactly<IllegalArgumentException> { Product(" ", ProductType.PHYSICAL, 1.99) }
            .message shouldBe "Product name must not be blank"
    }

    @Test
    fun `when price is lower than or equalTo 0, throw IllegalArgEx`() {
        shouldThrowExactly<IllegalArgumentException> { Product("product", ProductType.PHYSICAL, 0.0) }
            .message shouldBe "Produce price must be greaterThan 0"
    }

    @Test
    fun `when price has more than 2 digits, round up`() {
        val product = Product("product", ProductType.PHYSICAL, 1.965)
        product.price.toPlainString() shouldBe "1.97"
    }

    @Test
    fun `when two products have the same name and type, they should be equal`() {
        val productA = Product("product", ProductType.PHYSICAL, 19.99)
        val productB = Product("product", ProductType.PHYSICAL, 15.90)
        productA shouldBe productB
    }
}
