package hello.models.repositories

import hello.models.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by outcastgeek on 4/14/15.
 */
trait TagRepository extends JpaRepository[Tag, java.lang.Long] {
  def findByName(name: String, pageable: Pageable): java.util.List[Tag]
}

