package io.petproject.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.YearMonth

internal class PhysicalOrderTest {

    private lateinit var account: Account
    private lateinit var shippingAddress: Address
    private lateinit var paymentMethod: PaymentMethod
    private lateinit var physicalItems: List<Item>
    private lateinit var physicalTaxFreeItems: List<Item>

    @BeforeEach
    fun setup() {
        shippingAddress = Address.Builder()
                .country("Brazil")
                .city("Sao Paulo")
                .state("SP")
                .zipCode("01000-000")
                .streetAddress("Av Paulista, 1000")
                .build()

        val billingAddress = shippingAddress

        account = Account("John Doe", "john.doe@domain.suffix")

        paymentMethod = CreditCard(
                "JOHN DOE",
                "123.456.789-00", 123,
                YearMonth.of(2027, 11),
                billingAddress
        )


        val console = Product("PS4 Slim 1TB", ProductType.PHYSICAL, 1899.00)
        val chair = Product("PDP Chair", ProductType.PHYSICAL, 399.00)
        val book = Product("Cracking the Code Interview", ProductType.PHYSICAL_TAX_FREE, 219.57)
        val anotherBook = Product("The Hitchhiker's Guide to the Galaxy", ProductType.PHYSICAL_TAX_FREE, 120.00)

        physicalItems = listOf(
            Item(console, 1),
            Item(chair, 2)
        )
        physicalTaxFreeItems = listOf(
            Item(book, 2),
            Item(anotherBook, 1)
        )
    }

    @Test
    fun `when creating a Physical Order, there must be only Physical items in the list`() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            val product = Product("digital product", ProductType.DIGITAL, 1.99)
            PhysicalOrder(listOf(Item(product, 1)), account)
        }
        assertThat(ex.message).isEqualTo("A Physical Order may only contain Physical items")
    }

}