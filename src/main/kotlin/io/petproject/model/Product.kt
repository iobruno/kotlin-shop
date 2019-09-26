package io.petproject.model

import java.math.BigDecimal

data class Product(val name: String, val type: ProductType, private val _price: Double) {

    val price: BigDecimal = BigDecimal(_price)

}

enum class ProductType {
    PHYSICAL,
    PHYSICAL_TAX_FREE,
    DIGITAL,
    SUBSCRIPTION
}