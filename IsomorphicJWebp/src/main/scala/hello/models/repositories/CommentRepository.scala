package hello.models.repositories

import hello.models.Comment
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by outcastgeek on 4/14/15.
 */
trait CommentRepository extends JpaRepository[Comment, java.lang.Long] {
  def findByAuthor(author: String, pageable: Pageable): java.util.List[Comment]
}

