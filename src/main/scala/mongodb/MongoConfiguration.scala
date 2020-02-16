package mongodb

trait MongoConfiguration {
  val host = "localhost"
  val port = "27017"
  val user = "admin"
  val password = "password"
  val uri: String = s"mongodb://$user:$password@$host:$port/"
}
