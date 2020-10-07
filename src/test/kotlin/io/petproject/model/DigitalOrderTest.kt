package io.petproject.model

import java.time.YearMonth
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DigitalOrderTest {

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

    private lateinit var digitalItems: List<Item>

    @BeforeEach
    fun setup() {
        val musicDigitalAlbum = Product("Stairway to Heaven", ProductType.DIGITAL, 5.00)
        val videoGameDigitalCopy = Product("Nier:Automata", ProductType.DIGITAL, 129.90)
        digitalItems = listOf(Item(musicDigitalAlbum, 1), Item(videoGameDigitalCopy, 4))
    }

    @Test
    fun `when creating a Digital Order, there must be only Digital items in the list`() {
        val product = Product("physical product", ProductType.PHYSICAL, 1.99)
        assertThatThrownBy { DigitalOrder(listOf(Item(product, 1)), account) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("A Digital Order may only contain Digital items")
    }

    @Test
    fun `when placing a DigitalOrder, there must be at least one item in the list`() {
        val order = DigitalOrder(listOf(), account).withPaymentMethod(paymentMethod)
        assertThatThrownBy { order.place() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("There must be at least one item to place the Order")
    }

    @Test
    fun `when placing a DigitalOrder, a paymentMethod must be informed`() {
        val order = DigitalOrder(digitalItems, account)
        assertThatThrownBy { order.place() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("A Payment method must be informed to place the Order")
    }

    @Test
    fun `when placing a Digital Order, subtotal should compute overall sum of all Item prices`() {
        val order = DigitalOrder(digitalItems, account)
        order.withPaymentMethod(paymentMethod).place()
        assertThat(order.subtotal().toPlainString()).isEqualTo("524.60")
    }

    @Test
    fun `when placing a Digital Order, total should compute subtotal plus discounts for Digital Items`() {
        val order = DigitalOrder(digitalItems, account)
        order.withPaymentMethod(paymentMethod).place()
        assertThat(order.grandTotal().toPlainString()).isEqualTo("514.60")
    }

    @Test
    fun `when paying for a Digital Order, throw IllegalStateEx if Status is not PENDING`() {
        val order = DigitalOrder(digitalItems, account).withPaymentMethod(paymentMethod)
        assertThatThrownBy { order.pay() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Order must be placed before it can be payed")
    }

    @Test
    fun `when paying for a Digital Order, Status should be updated to UNSENT once pay is successful`() {
        val order = DigitalOrder(digitalItems, account)
        order.withPaymentMethod(paymentMethod).place().pay()
        assertThat(order.status).isEqualTo(OrderStatus.UNSENT)
    }

    @Test
    fun `when paying for a Digital Order that was already payed, throw IllegalStateEx`() {
        val order =
            DigitalOrder(digitalItems, account).withPaymentMethod(paymentMethod).place().pay()

        assertThatThrownBy { order.pay() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Order Payment has been processed already")
    }

    @Test
    fun `when fulfilling a Digital Order, throw IllegalStateEx if Status is not PAYMENT_COMPLETE`() {
        val order = DigitalOrder(digitalItems, account)
        order.withPaymentMethod(paymentMethod).place()

        assertThatThrownBy { order.fulfill() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Order must be placed and payed before it can be fulfilled")
    }

    @Test
    fun `when fulfilling a Digital Order, Status should be updated to SENT`() {
        val order = DigitalOrder(digitalItems, account)
        order.withPaymentMethod(paymentMethod).place().pay().fulfill()
        assertThat(order.status).isEqualTo(OrderStatus.SENT)
    }

    @Test
    fun `when completing a Digital Order, throw IllegalStateEx if Status is not UNSENT`() {
        val order =
            DigitalOrder(digitalItems, account).withPaymentMethod(paymentMethod).place().pay()

        assertThatThrownBy { order.complete() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage(
                "Order must have been shipped/sent and confirmed, before it can be completed")
    }

    @Test
    fun `when completing a Digital Order, Status should be updated to REDEEMED`() {
        val order = DigitalOrder(digitalItems, account)
        order.withPaymentMethod(paymentMethod).place().pay().fulfill().complete()
        assertThat(order.status).isEqualTo(OrderStatus.REDEEMED)
    }
}
