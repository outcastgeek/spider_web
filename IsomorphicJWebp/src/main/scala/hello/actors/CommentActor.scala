package hello.actors

import akka.actor.{Actor, ActorRef, Cancellable}
import akka.event.Logging
import hello.actors.CommentActor.{Save, ByAuthor}
import hello.actors.Messages._
import hello.models.Comment
import hello.models.repositories.CommentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._
import scala.collection.immutable.Map
import scala.concurrent.duration._

/**
 * Created by bebby on 4/17/2015.
 */
@Component(value = "CommentActor")
@Scope(value = "prototype")
class CommentActor @Autowired() (commentRepository: CommentRepository) extends Actor {

  val log = Logging(context.system, this)

  val errMsg = "The Comment Repository is Currently Busy"

  def startTimer(origin: ActorRef):Cancellable = {
    implicit val ec = context.dispatcher
    context.system.scheduler.scheduleOnce(2 seconds, self, NoReply(origin))
  }

  def receive = {
    case Save(comment, asMap) =>
      val timeout = startTimer(sender)
      commentRepository.save(comment)
      if (asMap)
        sender ! ReplyMap(Map(CommentActor.replyKey -> comment))
      else
        sender ! Reply(comment)
      log.info(s"Persisted $comment")
      timeout.cancel()
    case Find(id) =>
      val timeout = startTimer(sender)
      val thing = commentRepository.findOne(id)
      sender ! ReplyMap(Map(CommentActor.replyKey -> thing))
      timeout.cancel()
    case ByAuthor(author) =>
      val timeout = startTimer(sender)
      val  commentList = commentRepository.findByAuthor(author, new PageRequest(0, 19))
      log.info(s"All Comment by $author=>")
      commentList.toList.foreach(thing => log.info(s"$thing"))
      sender ! ReplyMap(Map(CommentActor.replyKey -> commentList))
      timeout.cancel()
    case All(asMap) =>
      val timeout = startTimer(sender)
      val commentList = commentRepository.findAll()
      commentList.toList.foreach(thing => log.info(s"$thing"))
      //      val thingList = thingRepository.findAll().toList
      log.info(s"All Comments=>")
      //      thingList.foreach(thing => log.info(s"$thing"))
      //      thingList match {
      //        case head::tail => sender ! Reply(Map(CommentActor.replyKey -> head))
      //        case Nil => sender ! NoReply
      //      }
      if (asMap)
        sender ! ReplyMap(Map(CommentActor.replyKey -> commentList))
      else
        sender ! Reply(commentList)
      timeout.cancel()
    case NoReply(origin) =>
      log.info(errMsg)
      origin ! ReplyMap(Map("error" -> errMsg))
    case _ =>
      log.info("Received Unknown Message")
  }
}

object CommentActor extends CanReply {
  def replyKey: String = "comment_data"

  case class Save(comment: Comment, asMap: Boolean = false)
  case class ByAuthor(author: String)
}




