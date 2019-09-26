package io.petproject.model

import java.math.BigDecimal
import java.math.RoundingMode


interface Order {
    val items: List<Item>
    val feesAndDiscounts: Map<String, BigDecimal>
    val account: Account

    var paymentMethod: PaymentMethod
    val type: OrderType


    fun subtotal(): BigDecimal {
        return items.map { it.subtotal }
                .fold(BigDecimal.ZERO) { acc, value -> acc.plus(value) }
                .setScale(2, RoundingMode.HALF_UP)
    }

    fun feesAndDiscounts(): BigDecimal {
        return feesAndDiscounts.values
                .asSequence()
                .fold(BigDecimal.ZERO) { acc, value -> acc.plus(value) }
                .setScale(2, RoundingMode.HALF_UP)
    }

    fun grandTotal(): BigDecimal {
        return subtotal().plus(feesAndDiscounts())
    }

    fun withPaymentMethod(paymentMethod: PaymentMethod) = apply {
        this.paymentMethod = paymentMethod
    }

    fun place() = apply {
        require(items.isNotEmpty()) { "There must be at least one item to place the Order" }
    }

    fun pay(): Order
}

data class PhysicalOrder(override val items: List<Item>,
                         override val account: Account) : Order {

    override val feesAndDiscounts = HashMap<String, BigDecimal>()
    override lateinit var paymentMethod: PaymentMethod

    override val type: OrderType = OrderType.PHYSICAL
    lateinit var shippingAddress: Address

    val parcels: () -> List<Parcel> = {
        Parcel.breakdown(items, shippingAddress)
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
        super.place()
        require(this::shippingAddress.isInitialized) { "Shipping Address must be informed for Orders with physical delivery" }
        require(this::paymentMethod.isInitialized) { "A Payment method must be informed to place the Order" }

        this.feesAndDiscounts["shippingAndHandling"] = Parcel.shippingCostsOf(parcels())
        this.feesAndDiscounts["importationTaxes"] = Parcel.importationFeesOf(parcels())
    }

    override fun pay() = apply {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


data class DigitalOrder(override val items: List<Item>,
                        override val account: Account) : Order {

    override val feesAndDiscounts = HashMap<String, BigDecimal>()
    override lateinit var paymentMethod: PaymentMethod

    override val type = OrderType.DIGITAL

    init {
        require(items.count { it.product.type != ProductType.DIGITAL } == 0) {
            "A Digital Order may only contain Digital items"
        }
    }

    override fun withPaymentMethod(paymentMethod: PaymentMethod) = apply {
        super.withPaymentMethod(paymentMethod)
    }

    override fun place() = apply {
        super.place()
        require(this::paymentMethod.isInitialized) { "A Payment method must be informed to place the Order" }
        this.feesAndDiscounts["Voucher"] = BigDecimal("-10")
    }

    override fun pay() = apply {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


data class SubscriptionOrder(override val items: List<Item>,
                             override val account: Account) : Order {

    constructor(item: Item, account: Account): this(listOf(item), account)

    override val feesAndDiscounts = HashMap<String, BigDecimal>()
    override lateinit var paymentMethod: PaymentMethod

    override val type = OrderType.SUBSCRIPTION

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
