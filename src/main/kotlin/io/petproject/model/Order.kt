package io.petproject.model


interface Order {
    val items: List<Item>
    val account: Account
    var paymentMethod: PaymentMethod
    val type: OrderType

    fun withPaymentMethod(paymentMethod: PaymentMethod) = apply {
        this.paymentMethod = paymentMethod
    }

    fun place(): Order

    fun pay(): Order
}

data class PhysicalOrder(override val items: List<Item>,
                         override val account: Account) : Order {

    override val type = OrderType.PHYSICAL
    override lateinit var paymentMethod: PaymentMethod

    lateinit var shippingAddress: Address

    val parcels: () -> List<Parcel> = {
        listOf<Parcel>()
    }

    init {
        require(items.count {
            it.product.type != ProductType.PHYSICAL &&
            it.product.type != ProductType.PHYSICAL_TAX_FREE } == 0) {
            "A Physical Order may only contain Physical items"
        }
    }

    fun withShippingAddress(address: Address) = apply {
        this.shippingAddress = address
    }

    override fun withPaymentMethod(paymentMethod: PaymentMethod) = apply {
        super.withPaymentMethod(paymentMethod)
    }

    override fun place() = apply {
        require(this::shippingAddress.isInitialized)
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

    init {
        require(items.count { it.product.type != ProductType.DIGITAL } == 0) {
            "A Digital Order may only contain Digital items"
        }
    }

    override fun withPaymentMethod(paymentMethod: PaymentMethod) = apply {
        super.withPaymentMethod(paymentMethod)
    }

    override fun place() = apply {
        require(this::paymentMethod.isInitialized)
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

    init {
        require(items.count { it.product.type != ProductType.SUBSCRIPTION } == 0) {
            "A Membership Order may only contain Membership items"
        }
        require(items.count() == 1) {
            "A Membership Order may only contain one Membership subscription"
        }
    }

    override fun withPaymentMethod(paymentMethod: PaymentMethod) = apply {
        super.withPaymentMethod(paymentMethod)
    }

    override fun place() = apply {
        require(this::paymentMethod.isInitialized)
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

enum class OrderStatus(val code: Int = 0) {
    UNKNOWN,
    PENDING(100),
    NOT_SHIPPED(200),
    UNSENT(200),
    PENDING_ACTIVATION(200),
    SHIPPED(300),
    SENT(300),
    DELIVERED(400),
    REDEEMED(400),
    ACTIVATED(400)
}
