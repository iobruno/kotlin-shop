package io.petproject.model

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.petproject.model.OrderStatus.DELIVERED
import io.petproject.model.ProductType.DIGITAL
import io.petproject.model.ProductType.PHYSICAL
import io.petproject.model.ProductType.PHYSICAL_TAX_FREE
import io.petproject.model.ShippingLabel.TAX_FREE
import java.time.YearMonth

internal class PhysicalOrderTest : AnnotationSpec() {

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
        val anotherBook = Product("The Hitchhiker's Guide to the Galaxy", PHYSICAL_TAX_FREE, 120.00)
        physicalItems = listOf(Item(console, 1), Item(chair, 2))
        physicalTaxFreeItems = listOf(Item(book, 2), Item(anotherBook, 1))
    }

    @Test
    fun `when creating a Physical Order, there must be only Physical items in the list`() {
        val product = Product("digital product", DIGITAL, 1.99)
        shouldThrowExactly<IllegalArgumentException> { PhysicalOrder(listOf(Item(product, 1)), account) }
            .message shouldBe "A Physical Order may only contain Physical items"
    }

    @Test
    fun `when placing a PhysicalOrder, there must be at least one item in the list`() {
        val order = PhysicalOrder(listOf(), account)
            .withShippingAddress(shippingAddress)
            .withPaymentMethod(paymentMethod)
        shouldThrowExactly<IllegalArgumentException> { order.place() }
            .message shouldBe "There must be at least one item to place the Order"
    }

    @Test
    fun `when placing a Physical Order, a shippingAddress must be informed`() {
        val order = PhysicalOrder(physicalItems, account)
            .withPaymentMethod(paymentMethod)
        shouldThrowExactly<IllegalArgumentException> { order.place() }
            .message shouldBe "Shipping Address must be informed for Orders with physical delivery"
    }

    @Test
    fun `when placing a PhysicalOrder, a paymentMethod must be informed`() {
        val order = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)

        shouldThrowExactly<java.lang.IllegalArgumentException> { order.place() }
            .message shouldBe "A Payment method must be informed to place the Order"
    }

    @Test
    fun `when placing a Physical Order with Physical and Physical_Books, there should be different parcels`() {
        val physicalItems = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)
            .withPaymentMethod(paymentMethod)
            .place()
        val parcels = physicalOrder.parcels()
        parcels.size shouldBe 2
    }

    @Test
    fun `when placing a Physical Order with physical_books, its package must contain notes informing it's free of taxes`() {
        val physicalItems = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)
            .withPaymentMethod(paymentMethod)
            .place()
        physicalOrder.parcels().any { it.shippingLabel == TAX_FREE } shouldBe true
    }

    @Test
    fun `when placing a Physical Order, subtotal should compute overall sum of all Item prices`() {
        val physicalItems = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)
            .withPaymentMethod(paymentMethod)
            .place()
        physicalOrder.subtotal().toPlainString() shouldBe "3256.14"
    }

    @Test
    fun `when placing a Physical Order, total should compute subtotal plus shippingCosts`() {
        val physicalItems = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)
            .withPaymentMethod(paymentMethod)
            .place()
        physicalOrder.grandTotal().toPlainString() shouldBe "3276.14"
    }

    @Test
    fun `when paying for Physical Order, throw IllegalStateEx if Status is not PENDING`() {
        val physicalItems = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)
            .withPaymentMethod(paymentMethod)
        shouldThrowExactly<IllegalStateException> { physicalOrder.pay() }
            .message shouldBe "Order must be placed before it can be payed"
    }

    @Test
    fun `when paying for Physical Order, Status should be updated to NOT_SHIPPED once the payment is done`() {
        val physicalItems = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)
            .withPaymentMethod(paymentMethod)
            .place()
            .pay()
        physicalOrder.status shouldBe OrderStatus.NOT_SHIPPED
    }

    @Test
    fun `when paying for Physical Order that was already payed, throw IllegalArgEx`() {
        val physicalItems = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)
            .withPaymentMethod(paymentMethod)
            .place()
            .pay()
        shouldThrowExactly<IllegalStateException> { physicalOrder.pay() }
            .message shouldBe "Order Payment has been processed already"
    }

    @Test
    fun `when fulfilling a Physical Order, throw IllegalStateEx if Status is not PAYMENT_COMPLETE`() {
        val physicalItems = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)
            .withPaymentMethod(paymentMethod)
            .place()
        shouldThrowExactly<IllegalStateException> { physicalOrder.fulfill() }
            .message shouldBe "Order must be placed and payed before it can be fulfilled"
    }

    @Test
    fun `when fulfilling a Physical Order, Status should be updated to SHIPPED`() {
        val physicalItems = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)
            .withPaymentMethod(paymentMethod)
            .place()
            .pay()
            .fulfill()
        physicalOrder.status shouldBe OrderStatus.SHIPPED
    }

    @Test
    fun `when completing a Physical Order, throw IllegalStateEx if Status is not SHIPPED`() {
        val physicalItems = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)
            .withPaymentMethod(paymentMethod)
            .place()
            .pay()
        shouldThrowExactly<IllegalStateException> { physicalOrder.complete() }
            .message shouldBe "Order must have been shipped/sent and confirmed, before it can be completed"
    }

    @Test
    fun `when completing a Physical Order, Status should be updated to DELIVERED`() {
        val physicalItems = listOf(physicalItems, physicalTaxFreeItems).flatten()
        val physicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)
            .withPaymentMethod(paymentMethod)
            .place()
            .pay()
            .fulfill()
            .complete()
        physicalOrder.status shouldBe DELIVERED
    }
}
