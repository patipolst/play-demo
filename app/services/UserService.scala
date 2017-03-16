package services

import javax.inject._
import models._
import models.tables.UsersTable
import play.api.db.slick.DatabaseConfigProvider
import services.db._
import slick.jdbc.meta.MTable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@Singleton
class UserService @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends UsersTable with DbComponent {

  import dbConfig.profile.api._

  initializeTable()

  def initializeTable(): Unit = {
    val tables = List(users)
    val existingTables = db.run(MTable.getTables)
    val init = existingTables.flatMap(existing => {
      val names = existing.map(mt => mt.name.name)
      val createIfNotExist = tables.filter(table =>
        !names.contains(table.baseTableRow.tableName)).map(_.schema.create)
      db.run(DBIO.sequence(createIfNotExist))
    })
    Await.result(init, Duration.Inf)

    // db.run(
    //   users ++= Seq(
    //     User(None, "Boom", 22, "1"),
    //     User(None, "Siggy", 40, "2"),
    //     User(None, "Yok", 27, "2")
    //   )
    // )
  }

  def dropTable(): Unit = {
    val tables = List(users)
    val existingTables = db.run(MTable.getTables)
    val drop = existingTables.flatMap(existing => {
      val names = existing.map(mt => mt.name.name)
      val dropIfExists = tables.filter(table =>
        names.contains(table.baseTableRow.tableName)).map(_.schema.drop)
      db.run(DBIO.sequence(dropIfExists))
    })
    Await.result(drop, Duration.Inf)
  }

  def getAll: Future[Seq[User]] =
    db.run(users.result)

  // def getWithAddress(): Future[Seq[UserWithAddress]] =
  //   db.run(users.withAddress.result).map(_.map(UserWithAddress.tupled))

  def getById(id: String): Future[Option[User]] =
    db.run(users.filter(_.id === id).result.headOption)

  // def getWithAddressById(id: String): Future[Option[UserWithAddress]] =
  //   db.run(users.withAddress.result.headOption).map(_.map(UserWithAddress.tupled))

  def create(newUser: User): Future[User] =
    db.run((users returning users.map(_.id)
      into ((user, id) => user.copy(id = Some(id)))) += newUser)

  def update(id: String, userUpdate: UserUpdate): Future[Option[User]] =
    getById(id).flatMap {
      case Some(foundUser) =>
        val updatedUser = userUpdate.merge(foundUser)
        db.run(users.filter(_.id === id).update(updatedUser)).map(_ => Some(updatedUser))
      case None => Future.successful(None)
    }

  def delete(id: String): Future[Int] = db.run(users.filter(_.id === id).delete)
}
