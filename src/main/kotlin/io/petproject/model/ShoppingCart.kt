package io.petproject.model

import io.petproject.model.ProductType.DIGITAL
import io.petproject.model.ProductType.PHYSICAL
import io.petproject.model.ProductType.PHYSICAL_TAX_FREE
import io.petproject.model.ProductType.SUBSCRIPTION
import java.math.BigDecimal
import java.math.RoundingMode

class ShoppingCart {

    val items = HashMap<Product, Item>()

    val subtotal: BigDecimal by lazy {
        items.values.asSequence()
            .map(Item::subtotal)
            .fold(BigDecimal.ZERO) { acc, value -> acc.plus(value) }
            .setScale(2, RoundingMode.HALF_UP)
    }

    fun add(product: Product, quantity: Int) = apply {
        items.compute(product) { _, item ->
            item?.updateBy(quantity) ?: Item(product, quantity)
        }
    }

    fun updateQuantity(product: Product, quantity: Int) = apply {
        if (quantity == 0)
            delete(product)
        else {
            items.compute(product) { _, item ->
                item?.updateTo(quantity) ?: throw IllegalArgumentException("Product specified is not in the Cart")
            }
        }
    }

    fun delete(product: Product) = apply {
        if (items.containsKey(product)) {
            items.remove(product)
        } else {
            throw IllegalArgumentException("Product specified is not in the Cart")
        }
    }

    fun checkout(account: Account): List<Order> =
        items.values.groupBy {
            val productType = it.product.type
            if (productType == PHYSICAL_TAX_FREE) PHYSICAL else productType
        }.map { (type, items) ->
            when (type) {
                PHYSICAL, PHYSICAL_TAX_FREE -> listOf(PhysicalOrder(items, account))
                DIGITAL -> listOf(DigitalOrder(items, account))
                SUBSCRIPTION -> items.map { SubscriptionOrder(it, account) }
            }
        }.flatten()
}
