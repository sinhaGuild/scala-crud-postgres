# Scala Workflow

1. New project from Metals

2. Edit build.sbt to include packages and libraries and start the sbt server.

```sh
sbt
```

3. Create `Connection.scala`

```scala
    import slick.jdbc.PostgresProfile.api._

object Connection {
    val db = Database.forConfig("postgres")
}

```

4. Create `src/resources/application.conf`

For connecting to `postgres` service


```conf
postgres = {
  connectionPool = "HikariCP" 
  dataSourceClass = "org.postgresql.ds.PGSimpleDataSource" 
  properties = {
    serverName = "localhost"
    portNumber = "5432"
    databaseName ="postgres"
    user = "postgres"
    password = "postgres"
  }
  numThreads = 10
}

```

For connecting to `elephantsql` service

```conf
databaseUrl {
  dataSourceClass = "slick.jdbc.DatabaseUrlDataSource"
  properties = {
    driver = "org.postgresql.Driver"
    url = "postgres://username:password@abul.db.elephantsql.com/dbname"
  }
}

```

[Reference](https://blog.rockthejvm.com/slick/)



5. Create Models/Schema

```scala
//Schema - Create case class
case class Movie(id: Long, name: String, releaseDate: LocalDate, lengthInMin: Int)

object SlickTables {
//define methods for every single field in case class like you do in nexus-prisma
  def id = column[Long](n = "movie_id", options = O.PrimaryKey, O.AutoInc)
  def name = column[String](n = "name")
  def releaseDate = column[LocalDate]("release_date")
  def lengthInMin = column[Int]("length_in_min")

....

}

```

6. Override `star` method

```scala
override def * = (id, name, releaseDate, lengthInMin)<> (Movie.tupled, Movie.unapply)

```

7. Create API Entry points

```scala
    lazy val movieTable = TableQuery[MovieTable]
```


8. Create execution context as `PrivateExecutionContext` object to run queries from `main`

```scala
object PrivateExecutionContext {
  //Create execution context to use for executing the query.
  val executor = Executors.newFixedThreadPool(4)
  implicit val ec:ExecutionContext = ExecutionContext.fromExecutorService(executor)
}
```

9. INSERT function


Create an insert function with dummy data

```scala
import slick.jdbc.PostgresProfile.api._
  import PrivateExecutionContext._

  //dummy data
  val bahubali = Movie(1L, "Baahubali: The Beginning", LocalDate.of(2019, 11, 2), 300)

  def demoInsertMovie(): Unit = {
    val queryDescription = SlickTables.movieTable += bahubali
    val futureId: Future[Int] = Connection.db.run(queryDescription)

    futureId.onComplete {
      case Success(value) => println(s"Query was sucessful $value")
      case Failure(exception) => println(s"Query failed with exception $exception")
    }
  }

  ```

10. Invoke from `main` and run from sbt server

```scala
    def main(args: Array[String]): Unit = {
    demoInsertMovie()
  }
```

Run function from sbt server as `run` for single run or `~run` or `~compile` for watching changes.