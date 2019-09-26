package io.petproject.model

import java.lang.ClassCastException
import java.math.BigDecimal
import java.math.RoundingMode

data class Product(val name: String, val type: ProductType, private val _price: Double) {

    val price: BigDecimal = BigDecimal(_price).setScale(2, RoundingMode.HALF_UP)

    init {
        require(name.isNotBlank()) { "Product name must not be blank" }
        require(_price > 0.0) { "Produce price must be greaterThan 0" }
    }

    override fun equals(other: Any?): Boolean {
        return try {
            val that = other as Product
            (this.name == that.name) && (this.type == that.type)
        } catch (ex: ClassCastException) {
            false
        }
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

enum class ProductType {
    PHYSICAL,
    PHYSICAL_TAX_FREE,
    DIGITAL,
    SUBSCRIPTION
}