package io.petproject.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class ProductTest {

    @Test
    fun `when name is blank, throw IllegalArgEx`() {
        assertThatThrownBy { Product(" ", ProductType.PHYSICAL, 1.99) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Product name must not be blank")
    }

    @Test
    fun `when price is lower than or equalTo 0, throw IllegalArgEx`() {
        assertThatThrownBy { Product("product", ProductType.PHYSICAL, 0.0) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Produce price must be greaterThan 0")
    }

    @Test
    fun `when price has more than 2 digits, round up`() {
        val product = Product("product", ProductType.PHYSICAL, 1.965)
        assertThat(product.price.toPlainString()).isEqualTo("1.97")
    }

    @Test
    fun `when two products have the same name and type, they should be equal`() {
        val productA = Product("product", ProductType.PHYSICAL, 19.99)
        val productB = Product("product", ProductType.PHYSICAL, 15.90)
        assertThat(productA).isEqualTo(productB)
    }
}
