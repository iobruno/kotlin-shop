package io.petproject.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.YearMonth

internal class DigitalOrderTest {

    private lateinit var account: Account
    private lateinit var paymentMethod: PaymentMethod
    private lateinit var digitalItems: List<Item>


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

        val musicDigitalAlbum = Product("Stairway to Heaven", ProductType.DIGITAL, 5.00)
        val videoGameDigitalCopy = Product("Nier:Automata", ProductType.DIGITAL, 129.90)

        digitalItems = listOf(
            Item(musicDigitalAlbum, 1),
            Item(videoGameDigitalCopy, 4)
        )
    }

    @Test
    fun `when creating a Digital Order, there must be only Digital items in the list`() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            val product = Product("physical product", ProductType.PHYSICAL, 1.99)
            DigitalOrder(listOf(Item(product, 1)), account)
        }
        assertThat(ex.message).isEqualTo("A Digital Order may only contain Digital items")
    }

}