package models.tables

import models.User
import services.db._

trait UsersTable  extends AddressesTable {
  this: DbComponent =>
  import dbConfig.profile.api._

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def id = column[String]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def age = column[Int]("age")
    def addressId = column[String]("address_id")
    def * = (id.?, name, age, addressId) <> ((User.apply _).tupled, User.unapply)

    // def address = foreignKey("address", addressId, addresses)(_.id,
    //   onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
  }

  val users = TableQuery[Users]

  implicit class UserExtensions[C[_]](q: Query[Users, User, C]) {
    def withAddress = q.join(addresses).on(_.addressId === _.id)
  }
}
