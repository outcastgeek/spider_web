package hello.models

import javax.persistence._

import org.hibernate.validator.constraints.NotEmpty

import scala.beans.BeanProperty

/**
 * Created by outcastgeek on 4/13/15.
 */
@Entity
@Table(name = "data")
class Datum extends Serializable {

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
  var key:String = _

  @BeanProperty
  var value:String = _

  override def toString: String = s"Datum[id->$id, key->$key, value->$value]"
}




