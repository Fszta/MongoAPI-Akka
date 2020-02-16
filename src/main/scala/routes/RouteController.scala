package routes

import akka.http.scaladsl.model.{ContentType, HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import mongodb.MongoConnector._

import scala.util.{Failure, Success}

object RouteController {

  val mongoRoutes: Route = pathPrefix("mongodb") {
    concat(
      get {
        path("get_document") {
          parameters("dbName","collection", "fieldName", "value") {
            (dbName,collectionName,field, value) =>
              onComplete(getOneDocument(dbName,collectionName,field, value)) {
                case Success(jsonUser) =>
                  complete(StatusCodes.OK, HttpEntity(ContentType(MediaTypes.`application/json`), jsonUser))
                case Failure(_) =>
                  complete(StatusCodes.BadRequest)
              }
          }
        }
      } ~ get {
        path("get_collection") {
          parameters("dbName", "collection") {
            (dbName, collectionName) =>
              onComplete(getDocumentsFromCollection(dbName, collectionName)) {
                case Success(jsonCollection) =>
                  complete(StatusCodes.OK, HttpEntity(ContentType(MediaTypes.`application/json`), jsonCollection))
                case Failure(_) =>
                  complete(StatusCodes.BadRequest)
              }
          }
        }
      } ~ post {
        path("delete_document") {
          parameters("dbName", "collection", "fieldName", "value") {
            (db, collectionName, field, value) =>
              onComplete(deleteDocumentFromCollection(db, collectionName, field, value)) {
                case Success(_) =>
                  complete(StatusCodes.OK)
                case Failure(_) =>
                  complete(StatusCodes.BadRequest)
              }
          }
        }
      } ~ post {
        path("add_document") {
          parameters("dbName","collection","jsonContent") {
            (dbName, collectionName, jsonContent) =>
              onComplete(writeDocumentInCollection(dbName,collectionName,jsonContent)) {
                case Success(_) =>
                  complete(StatusCodes.OK)
                case Failure(_) =>
                  complete(StatusCodes.BadRequest)
              }
          }
        }
      }
    )
  }
}
