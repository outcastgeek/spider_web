package hello.models

import javax.persistence._

import org.hibernate.validator.constraints.NotEmpty

import scala.beans.BeanProperty

/**
 * Created by outcastgeek on 4/13/15.
 */
@Entity
@Table(name = "tags")
class Tag extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: Int = _

  @ManyToOne
  @JoinColumn(name = "thing_id")
  @BeanProperty
  var thing:Thing = _

  @BeanProperty
  @NotEmpty
  var name:String = _

  @BeanProperty
  var count:Int = _

  override def toString: String = s"Tag[id->$id, name->$name, count->$count]"
}




