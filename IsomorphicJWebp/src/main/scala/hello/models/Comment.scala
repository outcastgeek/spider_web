package hello.models

import javax.persistence._

import org.hibernate.validator.constraints.NotEmpty

import scala.beans.BeanProperty

/**
 * Created by outcastgeek on 4/13/15.
 */
@Entity
@Table(name = "comment")
class Comment extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: Int = _

  @BeanProperty
  @NotEmpty
  var author:String = _

  @BeanProperty
  var text:String = _

  override def toString: String = s"Comment[id->$id, name->$author, score->$text]"
}




