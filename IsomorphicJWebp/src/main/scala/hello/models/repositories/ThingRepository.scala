package hello.models.repositories

import hello.models.Thing
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by outcastgeek on 4/14/15.
 */
trait ThingRepository extends JpaRepository[Thing, java.lang.Long] {
  def findByName(name: String, pageable: Pageable): java.util.List[Thing]
}

