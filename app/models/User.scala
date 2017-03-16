package models

import play.api.libs.json._

case class User(
  id: Option[String] = None,
  name: String,
  age: Int,
  addressId: String
)

object User {
  implicit val userJsonFormat = Json.format[User]
}

case class UserUpdate(
  name: Option[String] = None,
  age: Option[Int] = None,
  addressId: Option[String] = None
) {
  def merge(oldUser: User): User =
    oldUser.copy(name = name.getOrElse(oldUser.name),
    age = age.getOrElse(oldUser.age),
    addressId = addressId.getOrElse(oldUser.addressId))
}

object UserUpdate {
  implicit val userUpdateJsonFormat = Json.format[UserUpdate]
}

case class UserWithAddress(
  user: User,
  address: Address
)

object UserWithAddress {
  implicit val UserWithAddressJsonFormat = Json.format[UserWithAddress]
}
