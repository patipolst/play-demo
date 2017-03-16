package models.responses

import play.api.libs.json._

case class ErrorResponse(
  errors: Seq[String],
  code: Int,
  status: String
)

object ErrorResponse {
  implicit val errorResponseJsonFormat = Json.format[ErrorResponse]
}
