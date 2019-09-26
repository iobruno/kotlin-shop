package io.petproject.model

import io.petproject.model.ProductType.*
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.function.BiFunction

class ShoppingCart {

    val items = HashMap<Product, Item>()

    val subtotal: () -> BigDecimal = {
        items.values.asSequence()
                .map { item -> item.subtotal }
                .fold(BigDecimal.ZERO) { acc, value ->acc.plus(value) }
                .setScale(2, RoundingMode.HALF_UP)
    }

    fun add(product: Product, quantity: Int) = apply {
        items.compute(product, BiFunction { _, item ->
            item?.addMore(quantity) ?:
            Item(product, quantity)
        })
    }

    fun updateQuantity(product: Product, quantity: Int) = apply {
        if (quantity == 0) {
            delete(product)
        } else {
            items.compute(product, BiFunction { _, item ->
                item?.updateTo(quantity) ?:
                throw IllegalArgumentException("Product specified is not in the Cart")
            })
        }
    }

    fun delete(product: Product) = apply {
        if (items.containsKey(product)) {
            items.remove(product)
        } else {
            throw IllegalArgumentException("Product specified is not in the Cart")
        }
    }

    fun checkout(account: Account): List<Order> {
        return items.values.asSequence()
                .groupBy {
                    if (it.product.type == PHYSICAL_TAX_FREE)
                        ProductType.PHYSICAL
                    else
                        it.product.type
                }.map { (type, items) ->
                    when(type) {
                        PHYSICAL -> listOf(PhysicalOrder(items, account))
                        DIGITAL -> listOf(DigitalOrder(items, account))
                        SUBSCRIPTION -> items.map { SubscriptionOrder(it, account) }
                        else -> throw IllegalStateException("Unmapped ProductType to Order")
                    }
                }.flatten()
    }

}
