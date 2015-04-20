package hello.models

import java.sql.Date
import javax.persistence.CascadeType
import javax.persistence._

import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.{LastModifiedDate, CreatedDate}

import scala.beans.BeanProperty

/**
 * Created by outcastgeek on 4/13/15.
 */
@Entity
@Table(name = "things")
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

  @Version
  @BeanProperty
  var version:Int = _

  @OneToMany(mappedBy = "thing", cascade = Array(CascadeType.ALL))
  @BeanProperty
  var data:java.util.List[Datum] = _

  @OneToMany(mappedBy = "thing", cascade = Array(CascadeType.ALL))
  @BeanProperty
  var tags:java.util.List[Tag] = _

  @CreatedDate
  @BeanProperty
  var createdAt:Date = _

  @LastModifiedDate
  @BeanProperty
  var updatedAt:Date = _

  override def toString: String = s"Thing[id->$id, name->$name, score->$score, version->$version]"
}




