package models.responses

import play.api.libs.json._

case class DataResponse(
  data: JsValue,
  code: Int,
  status: String
)

object DataResponse {
  implicit val dataResponseJsonFormat = Json.format[DataResponse]
}
