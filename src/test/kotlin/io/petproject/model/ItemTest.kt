package io.petproject.model

import java.math.BigDecimal
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class ItemTest {

    @Test
    fun `when quantity is lower than or equalTo 0, throw IllegalArgEx`() {
        assertThatThrownBy { Item(0, Product("product", ProductType.PHYSICAL, 1.99)) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Quantity must be greaterThan 0")
    }

    @Test
    fun `when querying for subtotal, compute unit_price x units`() {
        val item = Item(10, Product("product", ProductType.PHYSICAL, 1.99))
        assertThat(item.subtotal).isEqualTo(BigDecimal("19.90"))
    }

    @Test
    fun `when addMore is called, if qty is greaterThanZero, it should increment the quantity`() {
        val item = Item(10, Product("product", ProductType.PHYSICAL, 1.99))
        item.addMore(1)
        assertThat(item.quantity).isEqualTo(11)
    }

    @Test
    fun `when addMore is called, if qty is lowerThanOrEqualToZero, it should throw IllegalArgEx`() {
        val item = Item(10, Product("product", ProductType.PHYSICAL, 1.99))
        assertThatThrownBy { item.addMore(-1) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Quantity must be greaterThan 0")
    }

    @Test
    fun `when updateTo is called, if qty is greaterOrEqualToZero, it should overwrite the quantity`() {
        val item = Item(10, Product("product", ProductType.PHYSICAL, 1.99))
        item.updateTo(1)
        assertThat(item.quantity).isEqualTo(1)
    }

    @Test
    fun `when updateTo is called, if qty is lowerThanZero, it should throw IllegalArgEx`() {
        val item = Item(10, Product("product", ProductType.PHYSICAL, 1.99))
        assertThatThrownBy { item.updateTo(-1) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Quantity must be equalTo or greaterThan 0")
    }
}
