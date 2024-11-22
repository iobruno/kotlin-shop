package io.petproject.model

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.petproject.model.ProductType.PHYSICAL

internal class ItemTest : AnnotationSpec() {

    @Test
    fun `when quantity is lower than or equalTo 0, throw IllegalArgEx`() {
        shouldThrowExactly<IllegalArgumentException> { Item(0, Product("product", PHYSICAL, 1.99)) }
            .message shouldBe "Quantity must be greaterThan 0"
    }

    @Test
    fun `when querying for subtotal, compute unit_price x units`() {
        val item = Item(10, Product("product", PHYSICAL, 1.99))
        item.subtotal.toPlainString() shouldBe "19.90"
    }

    @Test
    fun `when updateBy is called, if qty is greaterThanZero, it should increment the quantity`() {
        val item = Item(10, Product("product", PHYSICAL, 1.99)).updateBy(1)
        item.quantity shouldBe 11
    }

    @Test
    fun `when updateBy is called, if qty is lowerThanZero, it should decrease the quantity`() {
        val item = Item(10, Product("product", PHYSICAL, 1.99)).updateBy(-1)
        item.quantity shouldBe 9
    }

    @Test
    fun `when updateTo is called, if qty is greaterOrEqualToZero, it should overwrite the quantity`() {
        val item = Item(10, Product("product", PHYSICAL, 1.99)).updateTo(1)
        item.quantity shouldBe 1
    }

    @Test
    fun `when updateTo is called, if qty is lowerThanZero, it should throw IllegalArgEx`() {
        val item = Item(10, Product("product", PHYSICAL, 1.99))
        shouldThrowExactly<IllegalArgumentException> { item.updateTo(-1) }
            .message shouldBe "Quantity must be greaterThan 0"
    }
}
