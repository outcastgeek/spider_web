package hello.models

import javax.persistence._

import org.hibernate.validator.constraints.NotEmpty

import scala.beans.BeanProperty

/**
 * Created by outcastgeek on 4/13/15.
 */
@Entity
@Table(name = "thing")
class Thing extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: Int = _

  @BeanProperty
  @NotEmpty
  var name:String = _

  @BeanProperty
  var score:Int = _

  @BeanProperty
  var version:Int = _

  override def toString: String = s"Thing[id->$id, name->$name, score->$score, version->$version]"
}




