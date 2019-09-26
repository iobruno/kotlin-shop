package io.petproject.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class ItemTest {

    @Test
    fun `when quantity is lower than or equalTo 0, throw IllegalArgEx`() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            val product = Product("product", ProductType.PHYSICAL, 1.99)
            Item(product, 0)
        }
        assertThat(ex.message).isEqualTo("Quantity must be greaterThan 0")
    }

    @Test
    fun `when querying for subtotal, compute unit_price x units`() {
        val product = Product("product", ProductType.PHYSICAL, 1.99)
        val item = Item(product, 10)
        assertThat(item.subtotal.toPlainString()).isEqualTo("19.90")
    }

}