package io.petproject.model

data class Parcel(val items: List<Item>,
                  val shippingAddress: Address,
                  val shippingLabel: ShippingLabel) {

    companion object {

        fun of(items: List<Item>, shippingAddress: Address): List<Parcel> {
            TODO("implement parsing of items into parcels")
        }
    }

}

enum class ShippingLabel(val description: String = "") {
    DEFAULT,
    TAX_FREE
}