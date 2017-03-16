import models.User
import org.h2.command.ddl.DropDatabase
import services.UserService
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.DatabaseConfigProvider
import play.api.Application
import play.api.libs.json.Json
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class UserSpec extends PlaySpec with GuiceOneAppPerTest {

  trait UserExist {
    createUsersList(1)
  }

  trait DropDatabase {
    val userService = app.injector.instanceOf[UserService]
    userService.dropTable
  }

  def createUsersList(size: Int): Seq[User] = {
    val userService = app.injector.instanceOf[UserService]
    val createdUsers: Seq[Future[User]] = (1 to size).map { i =>
      User(None, s"Name $i", i, "1")
    }.map(userService.create)
    Await.result(Future.sequence(createdUsers), 10.seconds)
  }

  "UserController" should {

    "get empty users" in {
      val usersRoute = route(app, FakeRequest(GET, "/users")).get

      status(usersRoute) mustBe NOT_FOUND
      contentType(usersRoute) mustBe Some("application/json")
    }

    "get users" in new UserExist {
      val usersRoute = route(app, FakeRequest(GET, "/users")).get

      status(usersRoute) mustBe OK
      contentType(usersRoute) mustBe Some("application/json")
    }

    "get empty user by id" in {
      val usersRoute = route(app, FakeRequest(GET, "/users/1")).get

      status(usersRoute) mustBe NOT_FOUND
      contentType(usersRoute) mustBe Some("application/json")
    }

    "get user by id" in new UserExist {
      val usersRoute = route(app, FakeRequest(GET, "/users/1")).get

      status(usersRoute) mustBe OK
      contentType(usersRoute) mustBe Some("application/json")
    }

    "create user [invalid format]" in {
      val json = """{
      """
      val usersRoute = route(app, FakeRequest(POST, "/users").withBody(json)).get

      status(usersRoute) mustBe BAD_REQUEST
      contentType(usersRoute) mustBe Some("application/json")
    }

    "create user" in {
      val json = Json.parse("""{
        "name": "Boom",
        "age": 22,
        "addressId": "1"
      }""")
      val usersRoute = route(app, FakeRequest(POST, "/users").withJsonBody(json)).get

      status(usersRoute) mustBe OK
      contentType(usersRoute) mustBe Some("application/json")
    }

    "update user [invalid format]" in {
      val json = """{
      """
      val usersRoute = route(app, FakeRequest(PUT, "/users/1").withBody(json)).get

      status(usersRoute) mustBe BAD_REQUEST
      contentType(usersRoute) mustBe Some("application/json")
    }

    "update user" in new UserExist {
      val json = Json.parse("""{
        "name": "Boom",
        "age": 22,
        "addressId": "1"
      }""")
      val usersRoute = route(app, FakeRequest(PUT, "/users/1").withJsonBody(json)).get

      status(usersRoute) mustBe OK
      contentType(usersRoute) mustBe Some("application/json")
    }

    "delete empty user by id" in {
      val usersRoute = route(app, FakeRequest(DELETE, "/users/1")).get

      status(usersRoute) mustBe NOT_FOUND
      contentType(usersRoute) mustBe Some("application/json")
    }

    "delete user by id" in new UserExist {
      val usersRoute = route(app, FakeRequest(DELETE, "/users/1")).get

      status(usersRoute) mustBe OK
      contentType(usersRoute) mustBe Some("application/json")
    }

    "get users [faliure]" in new DropDatabase {
      val usersRoute = route(app, FakeRequest(GET, "/users")).get

      status(usersRoute) mustBe INTERNAL_SERVER_ERROR
      contentType(usersRoute) mustBe Some("application/json")
    }

    "get user by id [faliure]" in new DropDatabase {
      val usersRoute = route(app, FakeRequest(GET, "/users/1")).get

      status(usersRoute) mustBe INTERNAL_SERVER_ERROR
      contentType(usersRoute) mustBe Some("application/json")
    }

    "create user [faliure]" in new DropDatabase {
      val json = Json.parse("""{
        "name": "Boom",
        "age": 22,
        "addressId": "1"
      }""")
      val usersRoute = route(app, FakeRequest(POST, "/users").withJsonBody(json)).get

      status(usersRoute) mustBe INTERNAL_SERVER_ERROR
      contentType(usersRoute) mustBe Some("application/json")
    }

    "update user [faliure]" in new DropDatabase {
      val json = Json.parse("""{
        "name": "Boom",
        "age": 22,
        "addressId": "1"
      }""")
      val usersRoute = route(app, FakeRequest(PUT, "/users/1").withJsonBody(json)).get

      status(usersRoute) mustBe INTERNAL_SERVER_ERROR
      contentType(usersRoute) mustBe Some("application/json")
    }

    "delete user by id [faliure]" in new DropDatabase {
      val usersRoute = route(app, FakeRequest(DELETE, "/users/1")).get

      status(usersRoute) mustBe INTERNAL_SERVER_ERROR
      contentType(usersRoute) mustBe Some("application/json")
    }

  }

}
