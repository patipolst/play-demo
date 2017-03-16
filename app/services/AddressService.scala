package services

import models._
import models.tables.AddressesTable
import services.db._
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.jdbc.meta.MTable
import javax.inject._

@Singleton
class AddressService @Inject() (val dbConfigProvider: DatabaseConfigProvider) extends AddressesTable with DbComponent {
  import dbConfig.profile.api._

  initializeTable

  def initializeTable: Unit = {
    val tables = List(addresses)
    val existingTables = db.run(MTable.getTables)
    val init = existingTables.flatMap( existing => {
      val names = existing.map(mt => mt.name.name)
      val createIfNotExist = tables.filter( table =>
        (!names.contains(table.baseTableRow.tableName))).map(_.schema.create)
      db.run(DBIO.sequence(createIfNotExist))
    })
    Await.result(init, Duration.Inf)

    // db.run(
    //   addresses ++= Seq(
    //     Address(None, "Sukhumvit", "Bangkok"),
    //     Address(None, "Nimman", "Chiangmai"),
    //     Address(None, "Nimman", "Chiangmai")
    //   )
    // )
  }

  def dropTable: Unit = {
    val tables = List(addresses)
    val existingTables = db.run(MTable.getTables)
    val drop = existingTables.flatMap( existing => {
      val names = existing.map(mt => mt.name.name)
      val dropIfExists = tables.filter( table =>
        (names.contains(table.baseTableRow.tableName))).map(_.schema.drop)
      db.run(DBIO.sequence(dropIfExists))
    })
    Await.result(drop, Duration.Inf)
  }

  def getAddresses(): Future[Seq[Address]] = db.run(addresses.result)

  def getAddressById(id: String): Future[Option[Address]] =
    db.run(addresses.filter(_.id === id).result.headOption)

  def createAddress(newAddress: Address): Future[Address] =
    db.run((addresses returning addresses.map(_.id)
      into ((address, id) => address.copy(id = Some(id)))) += newAddress)

  def updateAddress(id: String, addressUpdate: AddressUpdate): Future[Option[Address]] =
    getAddressById(id).flatMap {
      case Some(foundAddress) =>
        val updatedAddress = addressUpdate.merge(foundAddress)
        db.run(addresses.filter(_.id === id).update(updatedAddress)).map(_ => Some(updatedAddress))
      case None => Future.successful(None)
    }

  def deleteAddress(id: String): Future[Int] = db.run(addresses.filter(_.id === id).delete)
}
