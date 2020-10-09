package io.petproject.model

import java.math.BigDecimal

data class Invoice(private val order: Order) {

    val items: List<Item> = order.items
    val subtotal: BigDecimal = order.subtotal()
    val otherCosts: Map<String, BigDecimal> = order.feesAndDiscounts
    val grandTotal: BigDecimal = order.grandTotal()
    val billingAddress: Address = order.paymentMethod.billingAddress

    val parcels: List<Parcel> by lazy {
        when(order) {
            is PhysicalOrder -> order.parcels()
            else -> listOf()
        }
    }
}