package io.petproject.model

import java.math.BigDecimal

class ShoppingCart {

    val items = HashMap<Product, Item>()

    val subtotal: () -> BigDecimal = {
        TODO("implement computation of subtotal")
    }

    fun add(product: Product, quantity: Int) = apply { }

    fun updateQuantity(product: Product, quantity: Int) = apply { }

    fun delete(product: Product) = apply { }

    fun checkout(account: Account): List<Order> {
        TODO("implement checkout for a variety of items in the cart")
    }

}