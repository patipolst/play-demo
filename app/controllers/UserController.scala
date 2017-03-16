package controllers

import javax.inject._

import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import services.UserService
import utils.Helper._

import scala.concurrent.Future

@Singleton
class UserController @Inject()(userService: UserService) extends Controller {
  val DatabaseException = "Database exception has occured"
  val UsersNotFound = "Users not Found"
  val MalformedJson = "Malformed Json"

  def create(): Action[AnyContent] = Action.async { request =>
    request.body.asJson match {
      case Some(json) =>
        userService.create(json.as[User]) map { user =>
          dataResponse(Json.toJson(user), OK)
        } recover {
          case _ => errorResponse(DatabaseException, INTERNAL_SERVER_ERROR)
        }
      case None => Future.successful(errorResponse(MalformedJson, BAD_REQUEST))
    }
  }

  def getAll: Action[AnyContent] = Action.async {
    userService.getAll map {
      case Nil => errorResponse(UsersNotFound, NOT_FOUND)
      case results@_ => dataResponse(Json.toJson(results), OK) //; Ok(views.html.users.index(results))
    } recover {
      case _ => errorResponse(DatabaseException, INTERNAL_SERVER_ERROR)
    }
  }

  def get(id: String): Action[AnyContent] = Action.async {
    userService.getById(id) map {
      case None => errorResponse(UsersNotFound, NOT_FOUND)
      case Some(user) => dataResponse(Json.toJson(user), OK)
    } recover {
      case _ => errorResponse(DatabaseException, INTERNAL_SERVER_ERROR)
    }
  }

  def update(id: String): Action[AnyContent] = Action.async { request =>
    request.body.asJson match {
      case Some(json) =>
        userService.update(id, json.as[UserUpdate]) map { user =>
          dataResponse(Json.toJson(user), OK)
        } recover {
          case _ => errorResponse(DatabaseException, INTERNAL_SERVER_ERROR)
        }
      case None => Future.successful(errorResponse(MalformedJson, BAD_REQUEST))
    }
  }

  def delete(id: String): Action[AnyContent] = Action.async {
    userService.delete(id) map {
      case 0 => errorResponse(UsersNotFound, NOT_FOUND)
      case 1 => dataResponse(Json.toJson(s"User UD: $id deleted"), OK)
    } recover {
      case _ => errorResponse(DatabaseException, INTERNAL_SERVER_ERROR)
    }
  }
}
