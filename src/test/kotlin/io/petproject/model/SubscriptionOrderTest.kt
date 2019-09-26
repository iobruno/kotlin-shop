package io.petproject.model

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.YearMonth

internal class SubscriptionOrderTest {
    private lateinit var account: Account
    private lateinit var paymentMethod: PaymentMethod
    private lateinit var subscriptions: List<Item>

    @BeforeEach
    fun setup() {
        val billingAddress = Address.Builder()
                .country("Brazil")
                .city("Sao Paulo")
                .state("SP")
                .zipCode("01000-000")
                .streetAddress("Av Paulista, 1000")
                .build()

        paymentMethod = CreditCard(
            "JOHN DOE",
            "123.456.789-00", 123,
            YearMonth.of(2027, 11),
            billingAddress
        )

        account = Account("John Doe", "john.doe@domain.suffix")

        val netflix = Product("Netflix Familiar Plan", ProductType.SUBSCRIPTION, 29.90)
        val spotify = Product("Spotify Premium", ProductType.SUBSCRIPTION, 14.90)
        val amazon = Product("Amazon Prime", ProductType.SUBSCRIPTION, 12.90)

        subscriptions = listOf(
            Item(netflix, 1),
            Item(spotify, 1),
            Item(amazon, 1)
        )
    }

    @Test
    fun `when creating a Membership Order, there must be only a Membership item the list`() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            val product = Product("physical product", ProductType.PHYSICAL, 1.99)
            SubscriptionOrder(listOf(Item(product, 1)), account)
        }
        Assertions.assertThat(ex.message).isEqualTo("A Membership Order may only contain Membership items")
    }

}