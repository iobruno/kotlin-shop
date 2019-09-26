package io.petproject.model

interface Order {
    val type: OrderType
    val items: List<Item>
    val account: Account
}

data class PhysicalOrder(override val items: List<Item>,
                         override val account: Account) : Order {

    override val type = OrderType.PHYSICAL


}

data class DigitalOrder(override val items: List<Item>,
                        override val account: Account) : Order {

    override val type = OrderType.DIGITAL

}

data class SubscriptionOrder(override val items: List<Item>,
                             override val account: Account) : Order {

    constructor(item: Item, account: Account): this(listOf(item), account)

    override val type = OrderType.SUBSCRIPTION

}

enum class OrderType {
    PHYSICAL,
    DIGITAL,
    SUBSCRIPTION
}