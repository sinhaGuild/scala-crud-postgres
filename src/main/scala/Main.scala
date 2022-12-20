import java.time.LocalDate
import scala.util.{Success, Failure, Try}
import scala.concurrent.{ExecutionContext, Future}
import java.util.concurrent.Executors

object PrivateExecutionContext {
  //Create execution context to use for executing the query.
  val executor = Executors.newFixedThreadPool(4)
  implicit val ec:ExecutionContext = ExecutionContext.fromExecutorService(executor)
}


object Main { 
  
  //  import everything from the slick pg-api package
  // import execution context
  import slick.jdbc.PostgresProfile.api._
  import PrivateExecutionContext._

  //dummy data
  val bahubali = Movie(1L, "Baahubali: The Beginning", LocalDate.of(2019, 11, 2), 300)
  val bahubali2 = Movie(1L, "Baahubali: The Conclusion", LocalDate.of(2020, 11, 2), 300)
  val matrix = Movie(1L, "Matrix", LocalDate.of(2007, 11, 2), 157)

  val amitabh = Actor(1L, "Amitabh Bachhan")
  val ashok = Actor(1L, "Ashok Kumar")

  
  //  CREATE

  //  Movie
  def demoInsertMovie(): Unit = {
    val queryDescription = SlickTables.movieTable += bahubali2
    val futureId: Future[Int] = Connection.db.run(queryDescription)

    futureId.onComplete {
      case Success(value) => println(s"Query was sucessful $value")
      case Failure(exception) => println(s"Query failed with exception $exception")
    }
    //  Sleep for x msecs to allow future Result to complete the query.
    Thread.sleep(10000)
  }

  //  READ
  def demoReadMovie(): Unit = {
    val futureResults: Future[Seq[Movie]] = Connection.db.run(SlickTables.movieTable.result)  // same as select *

    futureResults.onComplete{
      case Success(value) => println(s"Query was sucessful. Fetched: ${value.mkString(",")}")
      case Failure(exception) => println(s"Query failed with exception $exception")
    }
    //  Sleep for x msecs to allow future Result to complete the query.
    Thread.sleep(10000)
  }



    //  Multiple
  def multipleQueriesSingleTransaction(): Unit = {
    val insertMovie = SlickTables.movieTable += matrix
    val insertActorAmitabh = SlickTables.actorTable += amitabh
    val insertActorAshok = SlickTables.actorTable += ashok

    val finalQuery = DBIO.seq(insertMovie, insertActorAmitabh, insertActorAshok)

    val futureId: Future[Unit] = Connection.db.run(finalQuery.transactionally)

    futureId.onComplete {
      case Success(value) => println(s"All transactions were sucessfull. $value")
      case Failure(exception) => println(s"Query failed with exception $exception")
    }
    //  Sleep for x msecs to allow future Result to complete the query.
    Thread.sleep(10000)
  }


  //  Main Runner
  def main(args: Array[String]): Unit = {
    // demoInsertMovie()
    // demoReadMovie()
    multipleQueriesSingleTransaction()
    Thread.sleep(1000)
    PrivateExecutionContext.executor.shutdown()
  }

}