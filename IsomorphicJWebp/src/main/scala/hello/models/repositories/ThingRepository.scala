package hello.models.repositories

import hello.models.Thing
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by outcastgeek on 4/14/15.
 */
trait ThingRepository extends JpaRepository[Thing, java.lang.Long] {
  def getByName(name: String): Thing
}

