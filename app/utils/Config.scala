package utils

import com.typesafe.config.ConfigFactory
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait Config {
  private val config = ConfigFactory.load()
  val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("database")
}
