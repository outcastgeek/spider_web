package hello.models.repositories

import hello.models.Comment
//import org.springframework.cache.annotation.{CacheEvict, Cacheable}
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by outcastgeek on 4/14/15.
 */
trait CommentRepository extends JpaRepository[Comment, java.lang.Long] {

//  @CacheEvict(Array("commentCache"))
//  override def save(comment: Comment): Comment
//
//  @CacheEvict(Array("commentCache"))
//  override def save(comments: Iterable[Comment]): Iterable[Comment]
//
//  @Cacheable(Array("commentCache"))
  def findByAuthor(author: String, pageable: Pageable): java.util.List[Comment]
}

