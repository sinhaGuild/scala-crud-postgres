import java.time.LocalDate
import slick.lifted.ProvenShape
import scala.reflect.internal.util.TableDef
import slick.jdbc.PostgresProfile

//Schema file
case class Movie(id: Long, name: String, releaseDate: LocalDate, lengthInMin: Int)
case class Actor(id: Long, name: String)

object SlickTables {
  import slick.jdbc.PostgresProfile.api._


    class MovieTable(tag: Tag) extends Table[Movie](tag, Some("movies")/** Schema Name lowercase */, "Movie" /** Table Name Title case */){
        
        //define methods for every single field in case class like you do in nexus-prisma
        def id = column[Long](n = "movie_id", options = O.PrimaryKey, O.AutoInc)
        def name = column[String](n = "name")
        def releaseDate = column[LocalDate]("release_date")
        def lengthInMin = column[Int]("length_in_min")
        
        // Star method maps defined methods to the Movie constructor and destructor ie. mapping function to the case class.
        override def * = (id, name, releaseDate, lengthInMin)<> (Movie.tupled, Movie.unapply)
    }

    class ActorTable(tag: Tag) extends Table[Actor](tag, Some("movies")/** Schema Name lowercase */, "Actor" /** Table Name Title case */){
        
        //define methods for every single field in case class like you do in nexus-prisma
        def id = column[Long](n = "actor_id", options = O.PrimaryKey, O.AutoInc)
        def name = column[String](n = "name")
        
        // Star method maps defined methods to the Movie constructor and destructor ie. mapping function to the case class.
        override def * = (id, name)<> (Actor.tupled, Actor.unapply)
    }

    // API Entry points
    lazy val movieTable = TableQuery[MovieTable]

    lazy val actorTable = TableQuery[ActorTable]

}
