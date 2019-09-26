package io.petproject.model

data class Item(val product: Product, val quantity: Int) {

    val subtotal = product.price.times(quantity.toBigDecimal())

    init {
        require(quantity > 0) { "Quantity must be greaterThan 0" }
    }
}
