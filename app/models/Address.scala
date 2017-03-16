package models

import play.api.libs.json._

case class Address(
  id: Option[String] = None,
  street: String,
  city: String
)

object Address {
  implicit val addressJsonFormat = Json.format[Address]
}

case class AddressUpdate(
  street: Option[String] = None,
  city: Option[String] = None
) {
  def merge(oldAddress: Address): Address =
    oldAddress.copy(street = street.getOrElse(oldAddress.street),
    city = city.getOrElse(oldAddress.city))
}
