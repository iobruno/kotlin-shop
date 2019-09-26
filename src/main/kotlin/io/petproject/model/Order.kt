package io.petproject.model


interface Order {
    val items: List<Item>
    val account: Account
    var paymentMethod: PaymentMethod
    val type: OrderType

    fun withPaymentMethod(paymentMethod: PaymentMethod): Order

    fun place(): Order

    fun pay(): Order
}

data class PhysicalOrder(override val items: List<Item>,
                         override val account: Account) : Order {

    override val type = OrderType.PHYSICAL
    override lateinit var paymentMethod: PaymentMethod

    override fun withPaymentMethod(paymentMethod: PaymentMethod) = apply {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun place() = apply {
        require(this::paymentMethod.isInitialized)
    }

    override fun pay() = apply {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


data class DigitalOrder(override val items: List<Item>,
                        override val account: Account) : Order {

    override val type = OrderType.DIGITAL
    override lateinit var paymentMethod: PaymentMethod

    override fun withPaymentMethod(paymentMethod: PaymentMethod) = apply {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun place() = apply {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pay() = apply {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


data class SubscriptionOrder(override val items: List<Item>,
                             override val account: Account) : Order {

    constructor(item: Item, account: Account): this(listOf(item), account)

    override val type = OrderType.SUBSCRIPTION
    override lateinit var paymentMethod: PaymentMethod

    override fun withPaymentMethod(paymentMethod: PaymentMethod): Order {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun place() = apply {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pay() = apply {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

enum class OrderType {
    PHYSICAL,
    DIGITAL,
    SUBSCRIPTION
}