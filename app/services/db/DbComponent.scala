package services.db

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

trait DbComponent {
  val dbConfigProvider: DatabaseConfigProvider
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db: JdbcProfile#Backend#Database = dbConfig.db
}
