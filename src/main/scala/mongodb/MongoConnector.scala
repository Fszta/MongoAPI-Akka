package mongodb

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import utils.LoggerBase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object MongoConnector extends MongoConfiguration with LoggerBase {

  val client = MongoClient(uri)

  /**
   * Connection to a database using its name
   * @param dbName name of the target db
   * @return
   */
  def connectToDB(dbName: String): MongoDatabase = {
    val database = client.getDatabase(dbName)
    database
  }

  /**
   * Write a new document in collection from a jsonString
   * @param dbName name of the database
   * @param collectionName name of the collection
   * @param jsonContent json String to format to Document
   * @return
   */
  def writeDocumentInCollection(dbName: String, collectionName: String, jsonContent: String): Future[Unit] = Future {
    val database = connectToDB(dbName)
    val collection: MongoCollection[Document] = database.getCollection(collectionName)
    val document = Document(jsonContent)
    val insertion = collection.insertOne(document)
    val result = Await.result(insertion.toFuture(), 10.seconds)
    writeLog("info", s"Successfully add document to $collectionName")
    client.close()
  }

  /**
   * Delete document from collection using field value
   * @param dbName name of the database
   * @param collectionName
   * @param fieldName
   * @param value
   * @return
   */
  def deleteDocumentFromCollection(dbName: String, collectionName: String, fieldName: String, value: String): Future[Unit] = Future {
    val database = connectToDB(dbName)
    val collection: MongoCollection[Document] = database.getCollection(collectionName)
    Await.result(collection.deleteOne(equal(fieldName, value)).toFuture(), 10.seconds)
    writeLog("info", s"Delete document from $collectionName collection")
    client.close()
  }


  /**
   * Get all documents of a collection
   *
   * @param dbName name of the database
   * @param collectionName
   * @return
   */
  def getDocumentsFromCollection(dbName: String, collectionName: String): Future[String] = Future {
    val database = connectToDB(dbName)
    val collection: MongoCollection[Document] = database.getCollection(collectionName)
    val documents = Await.result(collection.find().toFuture(), 10.seconds)
    client.close()

    val json = documents.map(document => document.toJson())
    json.mkString("")
  }


  /**
   * Get one document from collection based on a
   * specific field value
   *
   * @param field
   * @param value
   * @return
   */
  def getOneDocument(dbName: String, collectionName: String, field: String, value: String): Future[String] = Future {
    val database = connectToDB(dbName)
    val collection: MongoCollection[Document] = database.getCollection(collectionName)
    val document = Await.result(collection.find(equal(field, value)).toFuture(),
      10.seconds)
    client.close()
    document(0).toJson()
  }
}
