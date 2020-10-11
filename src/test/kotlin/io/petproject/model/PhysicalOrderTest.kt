package io.petproject.model

import io.petproject.model.OrderStatus.DELIVERED
import io.petproject.model.ProductType.DIGITAL
import io.petproject.model.ProductType.PHYSICAL
import io.petproject.model.ProductType.PHYSICAL_TAX_FREE
import io.petproject.model.ShippingLabel.TAX_FREE
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.YearMonth

internal class PhysicalOrderTest {

    private val billingAddress: Address by lazy {
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
    private val shippingAddress = billingAddress

    private lateinit var physicalItems: List<Item>
    private lateinit var physicalTaxFreeItems: List<Item>

    @BeforeEach
    fun setup() {
        val console = Product("PS4 Slim 1TB", PHYSICAL, 1899.00)
        val chair = Product("PDP Chair", PHYSICAL, 399.00)
        val book = Product("Cracking the Code Interview", PHYSICAL_TAX_FREE, 219.57)
        val anotherBook =
            Product("The Hitchhiker's Guide to the Galaxy", PHYSICAL_TAX_FREE, 120.00)

        physicalItems = listOf(Item(console, 1), Item(chair, 2))
        physicalTaxFreeItems = listOf(Item(book, 2), Item(anotherBook, 1))
    }

    @Test
    fun `when creating a Physical Order, there must be only Physical items in the list`() {
        val product = Product("digital product", DIGITAL, 1.99)
        assertThatThrownBy { PhysicalOrder(listOf(Item(product, 1)), account) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("A Physical Order may only contain Physical items")
    }

    @Test
    fun `when placing a PhysicalOrder, there must be at least one item in the list`() {
        val order: PhysicalOrder =
            PhysicalOrder(listOf(), account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)

        assertThatThrownBy { order.place() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("There must be at least one item to place the Order")
    }

    @Test
    fun `when placing a Physical Order, a shippingAddress must be informed`() {
        val order: PhysicalOrder =
            PhysicalOrder(physicalItems, account)
                .withPaymentMethod(paymentMethod)

        assertThatThrownBy { order.place() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Shipping Address must be informed for Orders with physical delivery")
    }

    @Test
    fun `when placing a PhysicalOrder, a paymentMethod must be informed`() {
        val order: PhysicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)

        assertThatThrownBy { order.place() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("A Payment method must be informed to place the Order")
    }

    @Test
    fun `when placing a Physical Order with Physical and Physical_Books, there should be different parcels`() {
        val physicalItems: List<Item> = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder: PhysicalOrder =
            PhysicalOrder(physicalItems, account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)
                .place()

        val parcels = physicalOrder.parcels()
        assertThat(parcels.size).isEqualTo(2)
    }

    @Test
    fun `when placing a Physical Order with physical_books, its package must contain notes informing it's free of taxes`() {
        val physicalItems: List<Item> = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder: PhysicalOrder =
            PhysicalOrder(physicalItems, account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)
                .place()

        assertThat(physicalOrder.parcels().any { it.shippingLabel == TAX_FREE }).isTrue
    }

    @Test
    fun `when placing a Physical Order, subtotal should compute overall sum of all Item prices`() {
        val physicalItems: List<Item> = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder: PhysicalOrder =
            PhysicalOrder(physicalItems, account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)
                .place()

        assertThat(physicalOrder.subtotal().toPlainString()).isEqualTo("3256.14")
    }

    @Test
    fun `when placing a Physical Order, total should compute subtotal plus shippingCosts`() {
        val physicalItems: List<Item> = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder: PhysicalOrder =
            PhysicalOrder(physicalItems, account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)
                .place()

        assertThat(physicalOrder.grandTotal().toPlainString()).isEqualTo("3276.14")
    }

    @Test
    fun `when paying for Physical Order, throw IllegalStateEx if Status is not PENDING`() {
        val physicalItems: List<Item> = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder: PhysicalOrder =
            PhysicalOrder(physicalItems, account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)

        assertThatThrownBy { physicalOrder.pay() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Order must be placed before it can be payed")
    }

    @Test
    fun `when paying for Physical Order, Status should be updated to NOT_SHIPPED once the payment is done`() {
        val physicalItems: List<Item> = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder: PhysicalOrder =
            PhysicalOrder(physicalItems, account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)
                .place()
                .pay()

        assertThat(physicalOrder.status).isEqualTo(OrderStatus.NOT_SHIPPED)
    }

    @Test
    fun `when paying for Physical Order that was already payed, throw IllegalArgEx`() {
        val physicalItems: List<Item> = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder: PhysicalOrder =
            PhysicalOrder(physicalItems, account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)
                .place()
                .pay()

        assertThatThrownBy { physicalOrder.pay() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Order Payment has been processed already")
    }

    @Test
    fun `when fulfilling a Physical Order, throw IllegalStateEx if Status is not PAYMENT_COMPLETE`() {
        val physicalItems: List<Item> = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder: PhysicalOrder =
            PhysicalOrder(physicalItems, account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)
                .place()

        assertThatThrownBy { physicalOrder.fulfill() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Order must be placed and payed before it can be fulfilled")
    }

    @Test
    fun `when fulfilling a Physical Order, Status should be updated to SHIPPED`() {
        val physicalItems: List<Item> = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder: PhysicalOrder =
            PhysicalOrder(physicalItems, account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)
                .place()
                .pay()
                .fulfill()

        assertThat(physicalOrder.status).isEqualTo(OrderStatus.SHIPPED)
    }

    @Test
    fun `when completing a Physical Order, throw IllegalStateEx if Status is not SHIPPED`() {
        val physicalItems: List<Item> = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder: PhysicalOrder =
            PhysicalOrder(physicalItems, account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)
                .place()
                .pay()

        assertThatThrownBy { physicalOrder.complete() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Order must have been shipped/sent and confirmed, before it can be completed")
    }

    @Test
    fun `when completing a Physical Order, Status should be updated to DELIVERED`() {
        val physicalItems = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder =
            PhysicalOrder(physicalItems, account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)
                .place()
                .pay()
                .fulfill()
                .complete()

        assertThat(physicalOrder.status).isEqualTo(DELIVERED)
    }
}
