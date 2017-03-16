// package utils
//
// import models.formats._
// import com.wix.accord.dsl._
// import com.wix.accord._, ViolationBuilder._
// import io.circe.Json
//
// trait ValidationMethods {
//   def letter[ T <: String ]: Validator[ T ] =
//     new NullSafeValidator[ T ](
//     test    = _.forall(_.isLetter),
//     failure = _ -> s"must be letter"
//   )
//
//   def digit[ T <: String ]: Validator[ T ] =
//     new NullSafeValidator[ T ](
//     test    = _.forall(_.isDigit),
//     failure = _ -> s"must be digit"
//   )
//
//   def oneOf[ T <: AnyRef ]( options : Seq[T] ): Validator[ T ] =
//     new NullSafeValidator[ T ](
//       test    = options.contains(_),
//       failure = _ -> s"must be one of (${ options.mkString( ", " ) })"
//     )
//
//   def indexable[ T <: Json ]: Validator[ T ] =
//     new NullSafeValidator[ T ](
//     test    = _.hcursor.downField("id").as[String] match {
//       case Right(id) => true
//       case Left(error) => false
//     },
//     failure = _ -> s"must contain id as string"
//   )
//
//   def validIndexName[ T <: String ]: Validator[ T ] =
//     new NullSafeValidator[ T ](
//     test    = !_.contains("/"),
//     failure = _ -> s"must not contain / when creating mappings"
//   )
//
//
//   def jsonObject[ T <: Json ]: Validator[ T ] =
//     new NullSafeValidator[ T ](
//     test    = _.isObject,
//     failure = _ -> s"must be json object"
//   )
// }
//
// object Validator extends ValidationMethods with Config {
//   implicit val mappingsValidator = validator[Mappings] { mappings =>
//     mappings.index is notEmpty
//     mappings.index is validIndexName
//     mappings.mappings is jsonObject
//   }
//
//   implicit val indexDocumentValidator = validator[IndexDocument] { indexDocument =>
//     indexDocument.id is notEmpty
//   }
//
//   implicit val queryValidator = validator[Query] { query =>
//     query.indices.each is notEmpty
//     query.query is notEmpty
//   }
// }
