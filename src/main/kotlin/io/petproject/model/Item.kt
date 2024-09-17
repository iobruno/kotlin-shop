package io.petproject.model

data class Item(val product: Product, var quantity: Int) {

    constructor(quantity: Int, product: Product) : this(product, quantity)

    val subtotal by lazy { product.price.times(quantity.toBigDecimal()) }

    init {
        require(quantity > 0) { "Quantity must be greaterThan 0" }
    }

    fun updateBy(quantity: Int) = apply {
        updateTo(this.quantity+quantity)
    }

    fun updateTo(quantity: Int) = apply {
        require(quantity >= 0) { "Quantity must be greaterThan 0" }
        this.quantity = quantity
    }
}
