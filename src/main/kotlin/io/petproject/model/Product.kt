package io.petproject.model

import java.math.BigDecimal
import java.math.RoundingMode

data class Product(val name: String, val type: ProductType, private val _price: Double) {

    val price: BigDecimal by lazy { BigDecimal(_price).setScale(2, RoundingMode.HALF_UP) }

    init {
        require(name.isNotBlank()) { "Product name must not be blank" }
        require(_price > 0.0) { "Produce price must be greaterThan 0" }
    }

    override fun equals(other: Any?): Boolean =
        when (other) {
            is Product -> (this.name == other.name) && (this.type == other.type)
            else -> false
        }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

enum class ProductType(val id: Int) {
    PHYSICAL(100),
    PHYSICAL_TAX_FREE(100),
    DIGITAL(200),
    SUBSCRIPTION(300);
}
