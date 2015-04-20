package hello.models.repositories

import hello.models.{Datum, Thing}
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by outcastgeek on 4/14/15.
 */
trait DatumRepository extends JpaRepository[Datum, java.lang.Long] {
  def findByKey(key: String, pageable: Pageable): java.util.List[Datum]
}

