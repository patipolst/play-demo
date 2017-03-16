package utils

import models.responses._
import akka.util.ByteString
import play.api.mvc._
import play.api.http.HttpEntity
import play.api.libs.json._
import play.api.http.Status
import com.wix.accord._
import com.wix.accord.Descriptions.{ AccessChain, Indexed, Generic, SelfReference }

object Helper extends Helper
trait Helper {

  val MissingToken = "Missing JWT Token"
  val InvalidToken = "Invalid JWT Token"

  // implicit class JsValueHelper(json: JsValue) {
  //   def normalize(): JsValue = {
  //     val trimString: PartialFunction[JsValue, JsValue] = {
  //       case json => json.mapString(_.trim)
  //     }
  //     val transform: PartialFunction[JsValue, JsValue] = trimString
  //
  //     val jsonFields = json.hcursor.fields.getOrElse(Nil)
  //     jsonFields.foldLeft(json)((acc, field) =>
  //       acc.hcursor.downField(field).withFocus(transform).top match {
  //         case Some(result) => result
  //         case None => json
  //       }
  //     )
  //   }
  //
  //   def validateKeys(reqFields: List[String]): List[String] = {
  //     val jsonFields = json.hcursor.fields.getOrElse(Nil)
  //     reqFields.diff(jsonFields).map(f => s"$f is required") match {
  //       case Nil => "Malformed JsValue" :: Nil
  //       case errors => errors
  //     }
  //   }
  //
  //   def dropNullKeys(): JsValue =
  //     parse(Printer(
  //       preserveOrder = true,
  //       dropNullKeys = true,
  //       indent = "  "
  //     ).pretty(json)).getOrElse(JsValue.Null)
  // }

  // def validateModel[T](model: T)(implicit validator: Validator[T]): List[String] =
  //   validate(model) match {
  //     case Success => Nil
  //     case Failure(e) => e.map(x => x.description match {
  //       case AccessChain(Generic(field)) => s"$field: ${x.constraint}"
  //       case Indexed(i, AccessChain(Generic(field))) => s"$field: ${x.constraint}"
  //       case _ => s"${e.map(_.value).mkString}: ${x.constraint}"
  //     }).toList
  //   }

  def dataResponse(json: JsValue, status: Int): play.api.mvc.Result = {
    val jsonString = Json.stringify(Json.toJson(DataResponse(json, status, status)))
    createResponse(jsonString, status)
  }

  def errorResponse(error: String, status: Int): play.api.mvc.Result =
    errorResponse(error :: Nil, status)

  def errorResponse(errors: List[String], status: Int): play.api.mvc.Result = {
    val jsonString = Json.stringify(Json.toJson(ErrorResponse(errors, status, status)))
    createResponse(jsonString, status)
  }

  private def createResponse(jsonString: String, status: Int): play.api.mvc.Result = {
    Result(
      header = ResponseHeader(status),
      body = HttpEntity.Strict(ByteString(jsonString), Some("application/json"))
    )
  }

  private implicit def getStatusMessage(status: Int): String = status match {
    case 200 => "OK"
    case 201 => "Created"
    case 400 => "Bad Request"
    case 404 => "Not Found"
    case 500 => "Internal Server Error"
  }

}
