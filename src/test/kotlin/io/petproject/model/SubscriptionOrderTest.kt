package io.petproject.model

import io.petproject.model.OrderStatus.ACTIVATED
import io.petproject.model.OrderStatus.PENDING_ACTIVATION
import io.petproject.model.ProductType.PHYSICAL
import io.petproject.model.ProductType.SUBSCRIPTION
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.YearMonth

internal class SubscriptionOrderTest {

    private val billingAddress by lazy {
        Address.builder
            .withCountry("Brazil")
            .withCity("Sao Paulo")
            .withState("SP")
            .withZipCode("01000-000")
            .withStreetAddress("Av Paulista, 1000")
            .build()
    }
    private val paymentMethod: PaymentMethod by lazy {
        CreditCard("JOHN DOE", "123.456.789-00", 123, YearMonth.of(2027, 11), billingAddress)
    }
    private val account by lazy { Account("John Doe", "john.doe@domain.suffix") }
    private lateinit var subscriptions: List<Item>

    @BeforeEach
    fun setup() {
        val netflix = Product("Netflix Familiar Plan", SUBSCRIPTION, 29.90)
        val spotify = Product("Spotify Premium", SUBSCRIPTION, 14.90)
        val amazon = Product("Amazon Prime", SUBSCRIPTION, 12.90)
        subscriptions = listOf(Item(1, netflix), Item(2, spotify), Item(1, amazon))
    }

    @Test
    fun `when creating a Subscription Order, there must be only a Membership item the list`() {
        val product = Product("physical product", PHYSICAL, 1.99)

        assertThatThrownBy { SubscriptionOrder(listOf(Item(1, product)), account) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("A Membership Order may only contain Membership items")
    }

    @Test
    fun `when placing a Subscription, a paymentMethod must be informed`() {
        val order = SubscriptionOrder(subscriptions[0], account)

        assertThatThrownBy { order.place() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("A Payment method must be informed to place the Order")
    }

    @Test
    fun `when placing a Subscription, there must be exactly one item in the list`() {
        val order: SubscriptionOrder =
            SubscriptionOrder(subscriptions, account)
                .withPaymentMethod(paymentMethod)

        assertThatThrownBy { order.place() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("A Membership Order may only contain one Membership subscription")
    }

    @Test
    fun `when placing a Subscription Order, subtotal should be equalTo the Item price`() {
        val order: SubscriptionOrder =
            SubscriptionOrder(subscriptions[0], account)
                .withPaymentMethod(paymentMethod)
                .place()

        assertThat(order.subtotal().toPlainString()).isEqualTo("29.90")
    }

    @Test
    fun `when placing a Subscription Order, total should be equalTo the Subtotal`() {
        val order: SubscriptionOrder =
            SubscriptionOrder(subscriptions[0], account)
                .withPaymentMethod(paymentMethod)
                .place()

        assertThat(order.grandTotal().toPlainString()).isEqualTo("29.90")
    }

    @Test
    fun `when paying for Subscription Order, throw IllegalStateEx if Status is not PENDING`() {
        val order: SubscriptionOrder =
            SubscriptionOrder(subscriptions[0], account)
                .withPaymentMethod(paymentMethod)

        assertThatThrownBy { order.pay() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Order must be placed before it can be payed")
    }

    @Test
    fun `when paying for Subscription Order, Status should be updated to PENDING_ACTIVATION once pay is successful`() {
        val order: SubscriptionOrder =
            SubscriptionOrder(subscriptions[0], account)
                .withPaymentMethod(paymentMethod)
                .place()
                .pay()

        assertThat(order.status).isEqualTo(PENDING_ACTIVATION)
    }

    @Test
    fun `when paying for Subscription Order that was already payed, throw IllegalStateEx`() {
        val order: SubscriptionOrder =
            SubscriptionOrder(subscriptions[0], account)
                .withPaymentMethod(paymentMethod)
                .place()
                .pay()

        assertThatThrownBy { order.pay() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Order Payment has been processed already")
    }

    @Test
    fun `when fulfilling a Subscription Order, throw IllegalStateEx if Status is not PAYMENT_COMPLETE`() {
        val order: SubscriptionOrder =
            SubscriptionOrder(subscriptions[0], account)
                .withPaymentMethod(paymentMethod)
                .place()

        assertThatThrownBy { order.fulfill() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Order must be placed and payed before it can be fulfilled")
    }

    @Test
    fun `when fulfilling a Subscription Order, Status should be updated to ACTIVATED`() {
        val order: SubscriptionOrder =
            SubscriptionOrder(subscriptions[0], account)
                .withPaymentMethod(paymentMethod)
                .place()
                .pay()
                .fulfill()

        assertThat(order.status).isEqualTo(ACTIVATED)
    }
}
